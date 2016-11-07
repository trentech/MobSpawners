package com.gmail.trentech.mobspawners.data.spawner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.utils.ConfigManager;
import com.gmail.trentech.mobspawners.utils.SQLUtils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class SpawnerDB extends SQLUtils {

	private static ConcurrentHashMap<Location<World>, Spawner> cache = new ConcurrentHashMap<>();
	
	protected static void init() {
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

	protected static ConcurrentHashMap<Location<World>, Spawner> all() {
		return cache;
	}
	
	protected static void create(Spawner spawner) {
		if (spawner.getLocation().isPresent()) {
			Location<World> location = spawner.getLocation().get();
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			try {
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("INSERT into Spawners (Name, Spawner) VALUES (?, ?)");

				statement.setString(1, name);
				statement.setString(2, serialize(spawner));

				statement.executeUpdate();

				connection.close();

				cache.put(location, spawner);

				if (ConfigManager.get().getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
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
				Connection connection = getDataSource().getConnection();

				PreparedStatement statement = connection.prepareStatement("UPDATE Spawners SET Spawner = ? WHERE Name = ?");

				statement.setString(1, serialize(spawner));
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
					if (ConfigManager.get().getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
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

				spawner.setLocation(null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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
}
