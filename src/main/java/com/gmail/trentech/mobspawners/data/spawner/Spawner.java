package com.gmail.trentech.mobspawners.data.spawner;

import static com.gmail.trentech.mobspawners.data.DataQueries.AMOUNT;
import static com.gmail.trentech.mobspawners.data.DataQueries.ENABLE;
import static com.gmail.trentech.mobspawners.data.DataQueries.ENTITIES;
import static com.gmail.trentech.mobspawners.data.DataQueries.LOCATION;
import static com.gmail.trentech.mobspawners.data.DataQueries.OWNER;
import static com.gmail.trentech.mobspawners.data.DataQueries.RANGE;
import static com.gmail.trentech.mobspawners.data.DataQueries.TIME;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.data.DataQueries;
import com.gmail.trentech.mobspawners.data.LocationSerializable;
import com.gmail.trentech.mobspawners.utils.ConfigManager;
import com.gmail.trentech.mobspawners.utils.SQLUtils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class Spawner extends SQLUtils implements DataSerializable {

	private static ConcurrentHashMap<Location<World>, Spawner> cache = new ConcurrentHashMap<>();

	protected Optional<Location<World>> location = Optional.empty();
	protected List<EntityArchetype> entities = new ArrayList<>();
	protected int amount;
	protected int time;
	protected int radius;
	protected boolean enable = true;
	protected UUID owner = UUID.randomUUID();

	public Spawner() {
		ConfigurationNode config = ConfigManager.get().getConfig();
		
		this.amount = config.getNode("settings", "spawn-amount").getInt();
		this.time = config.getNode("settings", "time").getInt();
		this.radius = config.getNode("settings", "radius").getInt();
	}

	protected Spawner(List<EntityArchetype> entities, int amount, int time, int radius, boolean enable, UUID owner) {
		this.entities = entities;
		this.amount = amount;
		this.time = time;
		this.radius = radius;
		this.enable = enable;
		this.owner = owner;
	}

	protected Spawner(List<EntityArchetype> entities, Location<World> location, int amount, int time, int radius, boolean enable, UUID owner) {
		this.entities = entities;
		this.location = Optional.of(location);
		this.amount = amount;
		this.time = time;
		this.radius = radius;
		this.enable = enable;
		this.owner = owner;
	}

	public Optional<Location<World>> getLocation() {
		return location;
	}

	public void setLocation(Location<World> location) {
		this.location = Optional.of(location);
	}

	public List<EntityArchetype> getEntities() {
		return entities;
	}

	public void addEntity(EntityArchetype snapshot) {
		entities.add(snapshot);
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isEnabled() {
		return enable;
	}

	public void setEnabled(boolean enable) {
		this.enable = enable;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(Player player) {
		this.owner = player.getUniqueId();
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = new MemoryDataContainer().set(DataQueries.AMOUNT, amount).set(DataQueries.TIME, time).set(DataQueries.RANGE, radius).set(DataQueries.ENABLE, enable).set(DataQueries.OWNER, owner.toString());
		
		if(!entities.isEmpty()) {
			container.set(DataQueries.ENTITIES, entities);
		}

		if (location.isPresent()) {
			container.set(DataQueries.LOCATION, LocationSerializable.serialize(location.get()));
		}
		
		return container;
	}
	
	public void create() {
		if (location.isPresent()) {
			Location<World> location = this.location.get();
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into Spawners (Name, Spawner) VALUES (?, ?)");

				statement.setString(1, name);
				statement.setString(2, serialize(this));

				statement.executeUpdate();

				connection.close();

				cache.put(location, this);

				if (ConfigManager.get().getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
					if (Sponge.getServer().getPlayer(getOwner()).isPresent()) {
						Main.instance().spawn(this);
					}
				} else {
					Main.instance().spawn(this);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void update() {
		if (location.isPresent()) {
			Location<World> location = this.location.get();
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE Spawners SET Spawner = ? WHERE Name = ?");

				statement.setString(1, serialize(this));
				statement.setString(2, name);

				statement.executeUpdate();

				connection.close();

				cache.put(location, this);

				for (Task task : Sponge.getScheduler().getScheduledTasks()) {
					if (task.getName().startsWith("mobspawners:" + name)) {
						task.cancel();
					}
				}

				if (isEnabled()) {
					if (ConfigManager.get().getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
						if (Sponge.getServer().getPlayer(getOwner()).isPresent()) {
							Main.instance().spawn(this);
						}
					} else {
						Main.instance().spawn(this);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void remove() {
		if (location.isPresent()) {
			Location<World> location = this.location.get();
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("DELETE from Spawners WHERE Name = ?");

				statement.setString(1, name);
				statement.executeUpdate();

				connection.close();

				cache.remove(location);

				for (Task task : Sponge.getScheduler().getScheduledTasks()) {
					if (task.getName().startsWith("mobspawners:" + name)) {
						task.cancel();
					}
				}

				this.location = Optional.empty();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void init() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM Spawners");

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				Spawner spawner = deserialize(result.getString("Spawner"));

				cache.put(spawner.getLocation().get(), spawner);

				if (spawner.isEnabled()) {
					if (ConfigManager.get().getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
						if (Sponge.getServer().getPlayer(spawner.getOwner()).isPresent()) {
							Main.instance().spawn(spawner);
						}
					} else {
						Main.instance().spawn(spawner);
					}
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Optional<Spawner> get(Location<World> location) {
		if (all().containsKey(location)) {
			return Optional.of(all().get(location));
		}

		return Optional.empty();
	}

	public static List<Spawner> get(Player player) {
		List<Spawner> list = new ArrayList<>();

		for (Entry<Location<World>, Spawner> entry : all().entrySet()) {
			Spawner spawner = entry.getValue();

			if (spawner.getOwner().equals(player.getUniqueId())) {
				list.add(spawner);
			}
		}

		return list;
	}

	public static ConcurrentHashMap<Location<World>, Spawner> all() {
		return cache;
	}

	private static String serialize(Spawner spawner) {
		try {
			ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(spawner.toContainer());
			StringWriter stringWriter = new StringWriter();
			HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
			return stringWriter.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Spawner deserialize(String item) {
		try {
			ConfigurationNode node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
			DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);
			return Sponge.getDataManager().deserialize(Spawner.class, dataView).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static class Builder extends AbstractDataBuilder<Spawner> {

		public Builder() {
			super(Spawner.class, 1);
		}

		@Override
		protected Optional<Spawner> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(AMOUNT, TIME, RANGE, ENABLE, OWNER)) {
				List<EntityArchetype> entities = new ArrayList<>();
				
				if(container.contains(ENTITIES)) {
					 entities = container.getSerializableList(ENTITIES, EntityArchetype.class).get();
				}

				int amount = container.getInt(AMOUNT).get();
				int time = container.getInt(TIME).get();
				int range = container.getInt(RANGE).get();
				boolean enable = container.getBoolean(ENABLE).get();

				UUID owner = UUID.fromString(container.getString(OWNER).get());

				if (container.contains(LOCATION)) {
					Location<World> location = LocationSerializable.deserialize(container.getString(LOCATION).get());

					return Optional.of(new Spawner(entities, location, amount, time, range, enable, owner));
				} else {
					return Optional.of(new Spawner(entities, amount, time, range, enable, owner));
				}
			}

			return Optional.empty();
		}
	}
}
