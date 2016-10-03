package com.gmail.trentech.mobspawners.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
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

		if (configName.equalsIgnoreCase("recipes")) {
			for (EntityType entityType : Sponge.getRegistry().getAllOf(EntityType.class)) {
				if (Living.class.isAssignableFrom(entityType.getEntityClass()) && !(entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER))) {
					if (config.getNode(entityType.getId(), "spawner").isVirtual()) {
						config.getNode(entityType.getId(), "spawner", "1x1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
						config.getNode(entityType.getId(), "spawner", "1x2").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
						config.getNode(entityType.getId(), "spawner", "1x3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
						config.getNode(entityType.getId(), "spawner", "2x1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
						config.getNode(entityType.getId(), "spawner", "2x2").setValue(getItem(entityType));
						config.getNode(entityType.getId(), "spawner", "2x3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
						config.getNode(entityType.getId(), "spawner", "3x1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
						config.getNode(entityType.getId(), "spawner", "3x2").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
						config.getNode(entityType.getId(), "spawner", "3x3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
					}

					if (config.getNode(entityType.getId(), "module").isVirtual()) {
						config.getNode(entityType.getId(), "module", "1x1").setValue(ItemTypes.REDSTONE.getId());
						config.getNode(entityType.getId(), "module", "1x2").setValue(ItemTypes.REDSTONE.getId());
						config.getNode(entityType.getId(), "module", "1x3").setValue(ItemTypes.REDSTONE.getId());
						config.getNode(entityType.getId(), "module", "2x1").setValue(ItemTypes.GOLD_INGOT.getId());
						config.getNode(entityType.getId(), "module", "2x2").setValue(getItem(entityType));
						config.getNode(entityType.getId(), "module", "2x3").setValue(ItemTypes.GOLD_INGOT.getId());
						config.getNode(entityType.getId(), "module", "3x1").setValue(ItemTypes.REDSTONE.getId());
						config.getNode(entityType.getId(), "module", "3x2").setValue(ItemTypes.REDSTONE.getId());
						config.getNode(entityType.getId(), "module", "3x3").setValue(ItemTypes.REDSTONE.getId());
					}
				}
			}
			if (config.getNode("speed-module").isVirtual()) {
				config.getNode("speed-module", "1x1").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("speed-module", "1x2").setValue(ItemTypes.REPEATER.getId());
				config.getNode("speed-module", "1x3").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("speed-module", "2x1").setValue(ItemTypes.REDSTONE.getId());
				config.getNode("speed-module", "2x2").setValue(ItemTypes.DIAMOND.getId());
				config.getNode("speed-module", "2x3").setValue(ItemTypes.REDSTONE.getId());
				config.getNode("speed-module", "3x1").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("speed-module", "3x2").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("speed-module", "3x3").setValue(ItemTypes.GOLD_BLOCK.getId());
			}
			if (config.getNode("quantity-module").isVirtual()) {
				config.getNode("quantity-module", "1x1").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("quantity-module", "1x2").setValue(ItemTypes.COMPARATOR.getId());
				config.getNode("quantity-module", "1x3").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("quantity-module", "2x1").setValue(ItemTypes.REDSTONE.getId());
				config.getNode("quantity-module", "2x2").setValue(ItemTypes.DIAMOND.getId());
				config.getNode("quantity-module", "2x3").setValue(ItemTypes.REDSTONE.getId());
				config.getNode("quantity-module", "3x1").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("quantity-module", "3x2").setValue(ItemTypes.GOLD_BLOCK.getId());
				config.getNode("quantity-module", "3x3").setValue(ItemTypes.GOLD_BLOCK.getId());
			}

		} else if (configName.equalsIgnoreCase("config")) {
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

	private static String getItem(EntityType entityType) {
		if (entityType.equals(EntityTypes.CREEPER)) {
			return ItemTypes.SPAWN_EGG.getId() + ":50";
		} else if (entityType.equals(EntityTypes.SKELETON)) {
			return ItemTypes.SPAWN_EGG.getId() + ":51";
		} else if (entityType.equals(EntityTypes.SPIDER)) {
			return ItemTypes.SPAWN_EGG.getId() + ":52";
		} else if (entityType.equals(EntityTypes.ZOMBIE)) {
			return ItemTypes.SPAWN_EGG.getId() + ":54";
		} else if (entityType.equals(EntityTypes.SLIME)) {
			return ItemTypes.SPAWN_EGG.getId() + ":55";
		} else if (entityType.equals(EntityTypes.GHAST)) {
			return ItemTypes.SPAWN_EGG.getId() + ":56";
		} else if (entityType.equals(EntityTypes.PIG_ZOMBIE)) {
			return ItemTypes.SPAWN_EGG.getId() + ":57";
		} else if (entityType.equals(EntityTypes.ENDERMAN)) {
			return ItemTypes.SPAWN_EGG.getId() + ":58";
		} else if (entityType.equals(EntityTypes.CAVE_SPIDER)) {
			return ItemTypes.SPAWN_EGG.getId() + ":59";
		} else if (entityType.equals(EntityTypes.SILVERFISH)) {
			return ItemTypes.SPAWN_EGG.getId() + ":60";
		} else if (entityType.equals(EntityTypes.BLAZE)) {
			return ItemTypes.SPAWN_EGG.getId() + ":61";
		} else if (entityType.equals(EntityTypes.MAGMA_CUBE)) {
			return ItemTypes.SPAWN_EGG.getId() + ":62";
		} else if (entityType.equals(EntityTypes.BAT)) {
			return ItemTypes.SPAWN_EGG.getId() + ":65";
		} else if (entityType.equals(EntityTypes.WITCH)) {
			return ItemTypes.SPAWN_EGG.getId() + ":66";
		} else if (entityType.equals(EntityTypes.ENDERMITE)) {
			return ItemTypes.SPAWN_EGG.getId() + ":67";
		} else if (entityType.equals(EntityTypes.GUARDIAN)) {
			return ItemTypes.SPAWN_EGG.getId() + ":68";
		} else if (entityType.equals(EntityTypes.SHULKER)) {
			return ItemTypes.SPAWN_EGG.getId() + ":69";
		} else if (entityType.equals(EntityTypes.PIG)) {
			return ItemTypes.SPAWN_EGG.getId() + ":90";
		} else if (entityType.equals(EntityTypes.SHEEP)) {
			return ItemTypes.SPAWN_EGG.getId() + ":91";
		} else if (entityType.equals(EntityTypes.COW)) {
			return ItemTypes.SPAWN_EGG.getId() + ":92";
		} else if (entityType.equals(EntityTypes.CHICKEN)) {
			return ItemTypes.SPAWN_EGG.getId() + ":93";
		} else if (entityType.equals(EntityTypes.SQUID)) {
			return ItemTypes.SPAWN_EGG.getId() + ":94";
		} else if (entityType.equals(EntityTypes.WOLF)) {
			return ItemTypes.SPAWN_EGG.getId() + ":95";
		} else if (entityType.equals(EntityTypes.MUSHROOM_COW)) {
			return ItemTypes.SPAWN_EGG.getId() + ":96";
		} else if (entityType.equals(EntityTypes.OCELOT)) {
			return ItemTypes.SPAWN_EGG.getId() + ":98";
		} else if (entityType.equals(EntityTypes.HORSE)) {
			return ItemTypes.SPAWN_EGG.getId() + ":100";
		} else if (entityType.equals(EntityTypes.RABBIT)) {
			return ItemTypes.SPAWN_EGG.getId() + ":101";
		} else if (entityType.equals(EntityTypes.POLAR_BEAR)) {
			return ItemTypes.SPAWN_EGG.getId() + ":102";
		} else if (entityType.equals(EntityTypes.VILLAGER)) {
			return ItemTypes.SPAWN_EGG.getId() + ":120";
		} else if (entityType.equals(EntityTypes.ENDER_DRAGON)) {
			return ItemTypes.DRAGON_EGG.getId();
		} else if (entityType.equals(EntityTypes.WITHER)) {
			return ItemTypes.NETHER_STAR.getId();
		} else {
			return "NOT_SET";
		}
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
