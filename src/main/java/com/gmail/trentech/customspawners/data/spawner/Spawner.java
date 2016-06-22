package com.gmail.trentech.customspawners.data.spawner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.Main;
import com.gmail.trentech.customspawners.data.DataQueries;
import com.gmail.trentech.customspawners.utils.SQLUtils;
import com.gmail.trentech.customspawners.utils.Serializer;

public class Spawner extends SQLUtils implements DataSerializable {

	private static ConcurrentHashMap<String, Spawner> cache = new ConcurrentHashMap<>();

	protected List<EntityType> entities;
	protected Location<World> location;
	protected int amount;
	protected int time;
	protected int radius;
	protected boolean enable;

	public Spawner(List<EntityType> entities, Location<World> location, int amount, int time, int radius, boolean enable) {
		this.entities = entities;
		this.location = location;
		this.amount = amount;
		this.time = time;
		this.radius = radius;
		this.enable = enable;
	}

	public List<EntityType> getEntities() {
		return entities;
	}

	public void addEntity(EntityType entityType) {
		if (!entities.contains(entityType)) {
			entities.add(entityType);
		}
	}

	public Location<World> getLocation() {
		return location;
	}

	public void setLocation(Location<World> location) {
		this.location = location;
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

	public static Optional<Spawner> get(String name) {
		if (cache.containsKey(name)) {
			return Optional.of(cache.get(name));
		}

		return Optional.empty();
	}

	public static ConcurrentHashMap<String, Spawner> all() {
		return cache;
	}

	public void create(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into Spawners (Name, Spawner) VALUES (?, ?)");

			statement.setString(1, name);
			statement.setString(2, Serializer.serialize(this));

			statement.executeUpdate();

			connection.close();

			cache.put(name, this);

			Main.spawn(name, this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE Spawners SET Spawner = ? WHERE Name = ?");

			statement.setString(1, Serializer.serialize(this));
			statement.setString(2, name);

			statement.executeUpdate();

			connection.close();

			cache.put(name, this);

			for (Task task : Main.getGame().getScheduler().getScheduledTasks()) {
				if (task.getName().equals(name)) {
					task.cancel();
					break;
				}
			}

			if (isEnabled()) {
				Main.spawn(name, this);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void remove(String name) {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from Spawners WHERE Name = ?");

			statement.setString(1, name);
			statement.executeUpdate();

			connection.close();

			cache.remove(name);

			for (Task task : Main.getGame().getScheduler().getScheduledTasks()) {
				if (task.getName().equals(name)) {
					task.cancel();
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		try {
			Connection connection = getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM Spawners");

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String name = result.getString("Name");
				Spawner spawner = Serializer.deserialize(result.getString("Spawner"));

				cache.put(name, spawner);

				if (spawner.isEnabled()) {
					Main.spawn(name, spawner);
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getContentVersion() {
		return 2;
	}

	@Override
	public DataContainer toContainer() {
		String location = this.location.getExtent().getName() + "." + this.location.getBlockX() + "." + this.location.getBlockY() + "." + this.location.getBlockZ();
		List<String> entities = new ArrayList<>();

		for (EntityType type : this.entities) {
			entities.add(type.getId());
		}

		return new MemoryDataContainer().set(DataQueries.ENTITIES, entities).set(DataQueries.LOCATION, location).set(DataQueries.AMOUNT, amount).set(DataQueries.TIME, time).set(DataQueries.RANGE, radius).set(DataQueries.ENABLE, enable);
	}
}
