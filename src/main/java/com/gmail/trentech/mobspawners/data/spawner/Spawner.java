package com.gmail.trentech.mobspawners.data.spawner;

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
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.data.DataQueries;
import com.gmail.trentech.mobspawners.utils.ConfigManager;
import com.gmail.trentech.mobspawners.utils.SQLUtils;
import com.gmail.trentech.mobspawners.utils.Serializer;

import ninja.leaping.configurate.ConfigurationNode;

public class Spawner extends SQLUtils implements DataSerializable {

	private static ConcurrentHashMap<Location<World>, Spawner> cache = new ConcurrentHashMap<>();

	protected Optional<Location<World>> location = Optional.empty();
	protected List<EntityType> entities = new ArrayList<>();
	protected int amount;
	protected int time;
	protected int radius;
	protected boolean enable = true;
	protected UUID owner = UUID.randomUUID();

	protected Spawner() {}

	public Spawner(EntityType entity) {
		ConfigurationNode config = ConfigManager.get().getConfig();

		this.entities.add(entity);
		this.amount = config.getNode("settings", "spawn-amount").getInt();
		this.time = config.getNode("settings", "time").getInt();
		this.radius = config.getNode("settings", "radius").getInt();
	}

	protected Spawner(List<EntityType> entities, int amount, int time, int radius, boolean enable, UUID owner) {
		this.entities = entities;
		this.amount = amount;
		this.time = time;
		this.radius = radius;
		this.enable = enable;
		this.owner = owner;
	}

	protected Spawner(List<EntityType> entities, Location<World> location, int amount, int time, int radius, boolean enable, UUID owner) {
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

	public List<EntityType> getEntities() {
		return entities;
	}

	public void addEntity(EntityType entityType) {
		if (!entities.contains(entityType)) {
			entities.add(entityType);
		}
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

	public void create() {
		if (location.isPresent()) {
			Location<World> location = this.location.get();
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into Spawners (Name, Spawner) VALUES (?, ?)");

				statement.setString(1, name);
				statement.setString(2, Serializer.serialize(this));

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

				statement.setString(1, Serializer.serialize(this));
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
				Spawner spawner = Serializer.deserialize(result.getString("Spawner"));

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

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		List<String> entities = new ArrayList<>();

		for (EntityType type : this.entities) {
			entities.add(type.getId());
		}

		if (location.isPresent()) {
			Location<World> location = this.location.get();
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			return new MemoryDataContainer().set(DataQueries.LOCATION, name).set(DataQueries.ENTITIES, entities).set(DataQueries.AMOUNT, amount).set(DataQueries.TIME, time).set(DataQueries.RANGE, radius).set(DataQueries.ENABLE, enable).set(DataQueries.OWNER, owner.toString());
		} else {
			return new MemoryDataContainer().set(DataQueries.ENTITIES, entities).set(DataQueries.AMOUNT, amount).set(DataQueries.TIME, time).set(DataQueries.RANGE, radius).set(DataQueries.ENABLE, enable).set(DataQueries.OWNER, owner.toString());
		}
	}
}
