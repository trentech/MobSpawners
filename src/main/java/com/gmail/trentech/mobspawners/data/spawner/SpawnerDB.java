package com.gmail.trentech.mobspawners.data.spawner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.core.SQLManager;

public class SpawnerDB {

	private static ConcurrentHashMap<Location<World>, Spawner> cache = new ConcurrentHashMap<>();
	
	protected static void init() {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("SPAWNERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				Spawner spawner = deserialize(result.getBytes("Spawner"));

				cache.put(spawner.getLocation().get(), spawner);

				if (spawner.isEnabled()) {
					if (ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
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

	protected static ConcurrentHashMap<Location<World>, Spawner> all() {
		return cache;
	}
	
	protected static void create(Spawner spawner) {
		if (spawner.getLocation().isPresent()) {
			Location<World> location = spawner.getLocation().get();
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			try {
				SQLManager sqlManager = SQLManager.get(Main.getPlugin());
				Connection connection = sqlManager.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into " + sqlManager.getPrefix("SPAWNERS") + " (Name, Spawner) VALUES (?, ?)");

				statement.setString(1, name);
				statement.setBytes(2, serialize(spawner));

				statement.executeUpdate();

				connection.close();

				cache.put(location, spawner);

				if (ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
					if (Sponge.getServer().getPlayer(spawner.getOwner()).isPresent()) {
						Main.instance().spawn(spawner);
					}
				} else {
					Main.instance().spawn(spawner);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected static void update(Spawner spawner) {
		if (spawner.getLocation().isPresent()) {
			Location<World> location = spawner.getLocation().get();
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			try {
				SQLManager sqlManager = SQLManager.get(Main.getPlugin());
				Connection connection = sqlManager.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE " + sqlManager.getPrefix("SPAWNERS") + " SET Spawner = ? WHERE Name = ?");

				statement.setBytes(1, serialize(spawner));
				statement.setString(2, name);

				statement.executeUpdate();

				connection.close();

				cache.put(location, spawner);

				for (Task task : Sponge.getScheduler().getScheduledTasks()) {
					if (task.getName().startsWith("mobspawners:" + name)) {
						task.cancel();
					}
				}

				if (spawner.isEnabled()) {
					if (ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
						if (Sponge.getServer().getPlayer(spawner.getOwner()).isPresent()) {
							Main.instance().spawn(spawner);
						}
					} else {
						Main.instance().spawn(spawner);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected static void remove(Spawner spawner) {
		if (spawner.getLocation().isPresent()) {
			Location<World> location = spawner.getLocation().get();
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			try {
				SQLManager sqlManager = SQLManager.get(Main.getPlugin());
				Connection connection = sqlManager.getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("DELETE from " + sqlManager.getPrefix("SPAWNERS") + " WHERE Name = ?");

				statement.setString(1, name);
				statement.executeUpdate();

				connection.close();

				cache.remove(location);

				for (Task task : Sponge.getScheduler().getScheduledTasks()) {
					if (task.getName().startsWith("mobspawners:" + name)) {
						task.cancel();
					}
				}

				spawner.setLocation(Optional.empty());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static byte[] serialize(Spawner spawner) {
		try {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			GZIPOutputStream gZipOutStream = new GZIPOutputStream(byteOutStream);
			DataFormats.NBT.writeTo(gZipOutStream, spawner.toContainer());
			gZipOutStream.close();
			return byteOutStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}

	private static Spawner deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
			GZIPInputStream gZipInputSteam = new GZIPInputStream(byteInputStream);
			return Sponge.getDataManager().deserialize(Spawner.class, DataFormats.NBT.readFrom(gZipInputSteam)).get();
		} catch (InvalidDataFormatException | IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
