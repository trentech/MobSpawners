package com.gmail.trentech.mobspawners.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.item.ItemTypes;

import com.gmail.trentech.mobspawners.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private Path path;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	private static ConcurrentHashMap<String, ConfigManager> configManagers = new ConcurrentHashMap<>();

	private ConfigManager(String configName) {
		try {
			path = Main.instance().getPath().resolve(configName + ".conf");

			if (!Files.exists(path)) {
				Files.createFile(path);
				Main.instance().getLog().info("Creating new " + path.getFileName() + " file...");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		load();
	}

	public static ConfigManager get(String configName) {
		return configManagers.get(configName);
	}

	public static ConfigManager get() {
		return configManagers.get("config");
	}

	public static ConfigManager init() {
		return init("config");
	}

	public static ConfigManager init(String configName) {
		ConfigManager configManager = new ConfigManager(configName);
		CommentedConfigurationNode config = configManager.getConfig();

		if (configName.equalsIgnoreCase("config")) {
			if (config.getNode("recipes", "spawner").isVirtual()) {
				config.getNode("recipes", "spawner", "1x1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipes", "spawner", "1x2").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipes", "spawner", "1x3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipes", "spawner", "2x1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipes", "spawner", "2x2").setValue(ItemTypes.REPEATER.getId());
				config.getNode("recipes", "spawner", "2x3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipes", "spawner", "3x1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipes", "spawner", "3x2").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipes", "spawner", "3x3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
			}
			if (config.getNode("recipes", "speed-module").isVirtual()) {
				config.getNode("recipes", "speed-module", "1x1").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("recipes", "speed-module", "1x2").setValue(ItemTypes.REPEATER.getId());
				config.getNode("recipes", "speed-module", "1x3").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("recipes", "speed-module", "2x1").setValue(ItemTypes.REDSTONE.getId());
				config.getNode("recipes", "speed-module", "2x2").setValue(ItemTypes.DIAMOND.getId());
				config.getNode("recipes", "speed-module", "2x3").setValue(ItemTypes.REDSTONE.getId());
				config.getNode("recipes", "speed-module", "3x1").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("recipes", "speed-module", "3x2").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("recipes", "speed-module", "3x3").setValue(ItemTypes.GOLD_BLOCK.getId());
			}
			if (config.getNode("recipes", "quantity-module").isVirtual()) {
				config.getNode("recipes", "quantity-module", "1x1").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("recipes", "quantity-module", "1x2").setValue(ItemTypes.COMPARATOR.getId());
				config.getNode("recipes", "quantity-module", "1x3").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("recipes", "quantity-module", "2x1").setValue(ItemTypes.REDSTONE.getId());
				config.getNode("recipes", "quantity-module", "2x2").setValue(ItemTypes.DIAMOND.getId());
				config.getNode("recipes", "quantity-module", "2x3").setValue(ItemTypes.REDSTONE.getId());
				config.getNode("recipes", "quantity-module", "3x1").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("recipes", "quantity-module", "3x2").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("recipes", "quantity-module", "3x3").setValue(ItemTypes.GOLD_BLOCK.getId());
			}

			if (config.getNode("settings", "spawn-amount").isVirtual()) {
				config.getNode("settings", "spawn-amount").setValue(3);
			}
			if (config.getNode("settings", "time").isVirtual()) {
				config.getNode("settings", "time").setValue(60);
			}
			if (config.getNode("settings", "radius").isVirtual()) {
				config.getNode("settings", "radius").setValue(8);
			}
			if (config.getNode("settings", "disable-on-logout").isVirtual()) {
				config.getNode("settings", "disable-on-logout").setValue(true);
			}
			if (config.getNode("settings", "speed-module-increment").isVirtual()) {
				config.getNode("settings", "speed-module-increment").setValue(2);
			}
			if (config.getNode("settings", "quantity-module-increment").isVirtual()) {
				config.getNode("settings", "quantity-module-increment").setValue(1);
			}
		}

		configManager.save();

		configManagers.put(configName, configManager);

		return configManager;
	}

	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	private void load() {
		loader = HoconConfigurationLoader.builder().setPath(path).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}
}
