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
		
		if (configName.equalsIgnoreCase("config")) {
			if (config.getNode("recipe").isVirtual()) {
				config.getNode("recipe", "1x1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipe", "1x2").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipe", "1x3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipe", "2x1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipe", "2x2").setValue("%MOB%");
				config.getNode("recipe", "2x3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipe", "3x1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipe", "3x2").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
				config.getNode("recipe", "3x3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15");
			}
			if (config.getNode("settings", "spawn_amount").isVirtual()) {
				config.getNode("settings", "spawn_amount").setValue(3);
			}
			if (config.getNode("settings", "time").isVirtual()) {
				config.getNode("settings", "time").setValue(60);
			}
			if (config.getNode("settings", "radius").isVirtual()) {
				config.getNode("settings", "radius").setValue(8);
			}
			if (config.getNode("settings", "disable_on_logout").isVirtual()) {
				config.getNode("settings", "disable_on_logout").setValue(true);
			}
			for (EntityType entityType : Sponge.getRegistry().getAllOf(EntityType.class)) {
				if (Living.class.isAssignableFrom(entityType.getEntityClass()) && !(entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER))) {
					if(config.getNode("mob", entityType.getId()).isVirtual()) {
						if(entityType.equals(EntityTypes.CREEPER)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":50");
						} else if(entityType.equals(EntityTypes.SKELETON)) {
					    	config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":51");
					    } else if(entityType.equals(EntityTypes.SPIDER)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":52");
						} else if(entityType.equals(EntityTypes.ZOMBIE)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":54");
						} else if(entityType.equals(EntityTypes.SLIME)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":55");
						} else if(entityType.equals(EntityTypes.GHAST)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":56");
						} else if(entityType.equals(EntityTypes.PIG_ZOMBIE)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":57");
						} else if(entityType.equals(EntityTypes.ENDERMAN)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":58");
						} else if(entityType.equals(EntityTypes.CAVE_SPIDER)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":59");
						} else if(entityType.equals(EntityTypes.SILVERFISH)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":60");
						} else if(entityType.equals(EntityTypes.BLAZE)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":61");
						} else if(entityType.equals(EntityTypes.MAGMA_CUBE)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":62");
						} else if(entityType.equals(EntityTypes.BAT)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":65");
						} else if(entityType.equals(EntityTypes.WITCH)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":66");
						} else if(entityType.equals(EntityTypes.ENDERMITE)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":67");
						} else if(entityType.equals(EntityTypes.GUARDIAN)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":68");
					    } else if(entityType.equals(EntityTypes.SHULKER)) {
					    	config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":69");
						} else if(entityType.equals(EntityTypes.PIG)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":90");
						} else if(entityType.equals(EntityTypes.SHEEP)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":91");
						} else if(entityType.equals(EntityTypes.COW)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":92");
						} else if(entityType.equals(EntityTypes.CHICKEN)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":93");
						} else if(entityType.equals(EntityTypes.SQUID)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":94");
						} else if(entityType.equals(EntityTypes.WOLF)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":95");
						} else if(entityType.equals(EntityTypes.MUSHROOM_COW)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":96");
						} else if(entityType.equals(EntityTypes.OCELOT)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":98");
						} else if(entityType.equals(EntityTypes.HORSE)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":100");
						} else if(entityType.equals(EntityTypes.RABBIT)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":101");
						} else if(entityType.equals(EntityTypes.POLAR_BEAR)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":102");
						} else if(entityType.equals(EntityTypes.VILLAGER)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.SPAWN_EGG.getId() + ":120");
						} else if(entityType.equals(EntityTypes.ENDER_DRAGON)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.DRAGON_EGG.getId());
						} else if(entityType.equals(EntityTypes.WITHER)) {
							config.getNode("mob", entityType.getId()).setValue(ItemTypes.NETHER_STAR.getId());
						} else {
							config.getNode("mob", entityType.getId()).setValue("NOT_SET");
						}				
					}
				}
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
