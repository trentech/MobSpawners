package com.gmail.trentech.mobspawners.data.spawner;

import static com.gmail.trentech.mobspawners.data.DataQueries.AMOUNT;
import static com.gmail.trentech.mobspawners.data.DataQueries.ENABLE;
import static com.gmail.trentech.mobspawners.data.DataQueries.ENTITIES;
import static com.gmail.trentech.mobspawners.data.DataQueries.LOCATION;
import static com.gmail.trentech.mobspawners.data.DataQueries.OWNER;
import static com.gmail.trentech.mobspawners.data.DataQueries.RANGE;
import static com.gmail.trentech.mobspawners.data.DataQueries.TIME;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.data.DataQueries;
import com.gmail.trentech.mobspawners.data.LocationSerializable;
import com.gmail.trentech.mobspawners.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class Spawner implements DataSerializable {

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
		if (location == null) {
			this.location = Optional.empty();
		}

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

	public void create() {
		SpawnerDB.create(this);
	}
	
	public void update() {
		SpawnerDB.update(this);
	}
	
	public void remove() {
		SpawnerDB.remove(this);
	}
	
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = new MemoryDataContainer().set(DataQueries.AMOUNT, amount).set(DataQueries.TIME, time).set(DataQueries.RANGE, radius).set(DataQueries.ENABLE, enable).set(DataQueries.OWNER, owner.toString());

		if (!entities.isEmpty()) {
			container.set(DataQueries.ENTITIES, entities);
		}

		if (location.isPresent()) {
			container.set(DataQueries.LOCATION, LocationSerializable.serialize(location.get()));
		}

		return container;
	}

	public static class Builder extends AbstractDataBuilder<Spawner> {

		public Builder() {
			super(Spawner.class, 1);
		}

		@Override
		protected Optional<Spawner> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(AMOUNT, TIME, RANGE, ENABLE, OWNER)) {
				List<EntityArchetype> entities = new ArrayList<>();

				if (container.contains(ENTITIES)) {
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
	
	public static Optional<Spawner> get(Location<World> location) {
		if (SpawnerDB.all().containsKey(location)) {
			return Optional.of(SpawnerDB.all().get(location));
		}

		return Optional.empty();
	}

	public static List<Spawner> get(Player player) {
		List<Spawner> list = new ArrayList<>();

		for (Entry<Location<World>, Spawner> entry : SpawnerDB.all().entrySet()) {
			Spawner spawner = entry.getValue();

			if (spawner.getOwner().equals(player.getUniqueId())) {
				list.add(spawner);
			}
		}

		return list;
	}
	
	public static void init() {
		SpawnerDB.init();
	}
	
	public static ConcurrentHashMap<Location<World>, Spawner> all() {
		return SpawnerDB.all();
	}
}
