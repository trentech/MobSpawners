package com.gmail.trentech.mobspawners.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.spongepowered.api.item.ItemTypes;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.utils.Resource;
import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.core.RecipeManager;
import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.pjc.help.Argument;
import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjc.help.Usage;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Common {

	public static void init() {
		initConfig();
		initHelp();
		initData();
	}
	
	public static void initData() {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + sqlManager.getPrefix("SPAWNERS") + " (Name TEXT, Spawner TEXT)");
			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void initHelp() {
		Usage usageEntity = new Usage(Argument.of("<entity>", "Specifies an living entity"));
		
		Help spawnerCreate = new Help("spawner create", "create", "Temporary command to create mob spawner")
				.setPermission("mobspawners.cmd.spawner.create")
				.setUsage(usageEntity)
				.addExample("/spawner create minecraft:zombie");
		
		Help spawnerList = new Help("spawner list", "list", "List all spawners and their locations")
				.setPermission("mobspawners.cmd.spawner.list")
				.addExample("/spawner list");
		
		Help spawnerModuleEntity = new Help("spawner module entity", "entity", "Temporary command to create mob module")
				.setPermission("mobspawners.cmd.spawner.module.entity")
				.setUsage(usageEntity)
				.addExample("/spawner module entity minecraft:creeper");
		
		Help spawnerModuleQuantity = new Help("spawner module quantity", "quantity", "Temporary command to create quantity module")
				.setPermission("mobspawners.cmd.spawner.module.quantity")
				.addExample("/spawner module quantity");
		
		Help spawnerModuleSpeed = new Help("spawner module speed", "speed", "Temporary command to create speed module")
				.setPermission("mobspawners.cmd.spawner.module.speed")
				.addExample("/spawner module speed");
		
		Help spawnerModule = new Help("spawner module", "module", "Subcommand for modules")
				.setPermission("mobspawners.cmd.spawner.module")
				.addExample("/spawner module")
				.addChild(spawnerModuleSpeed)
				.addChild(spawnerModuleQuantity)
				.addChild(spawnerModuleEntity);
		
		Help spawner = new Help("spawner", "spawner", "Base command for MobSpawners")
				.setPermission("mobspawners.cmd.spawner")
				.addChild(spawnerList)
				.addChild(spawnerCreate)
				.addChild(spawnerModule);
		
		Help.register(spawner);
	}
	
	public static void initConfig() {
		ConfigManager configManager = ConfigManager.init(Main.getPlugin());
		CommentedConfigurationNode config = configManager.getConfig();

		if (config.getNode("recipes", "spawner").isVirtual()) {
			config.getNode("recipes", "spawner", "enable").setValue(true);
			config.getNode("recipes", "spawner", "id").setValue("spawner");
			config.getNode("recipes", "spawner", "row1").setValue(ItemTypes.STAINED_GLASS.getId() + ":15" + "," + ItemTypes.STAINED_GLASS.getId() + ":15" + "," + ItemTypes.STAINED_GLASS.getId() + ":15");
			config.getNode("recipes", "spawner", "row2").setValue(ItemTypes.STAINED_GLASS.getId() + ":15" + "," + ItemTypes.REPEATER.getId() + "," + ItemTypes.STAINED_GLASS.getId() + ":15");
			config.getNode("recipes", "spawner", "row3").setValue(ItemTypes.STAINED_GLASS.getId() + ":15" + "," + ItemTypes.STAINED_GLASS.getId() + ":15" + "," + ItemTypes.STAINED_GLASS.getId() + ":15");
		}
		if (config.getNode("recipes", "speed-module").isVirtual()) {
			config.getNode("recipes", "speed-module", "enable").setValue(true);
			config.getNode("recipes", "speed-module", "id").setValue("speed_module");
			config.getNode("recipes", "speed-module", "row1").setValue(ItemTypes.GOLD_BLOCK.getId() + "," + ItemTypes.REPEATER.getId() + "," + ItemTypes.GOLD_BLOCK.getId());
			config.getNode("recipes", "speed-module", "row2").setValue(ItemTypes.REDSTONE.getId() + "," + ItemTypes.DIAMOND.getId() + "," + ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "speed-module", "row3").setValue(ItemTypes.GOLD_BLOCK.getId() + "," + ItemTypes.GOLD_BLOCK.getId() + "," + ItemTypes.GOLD_BLOCK.getId());
		}
		if (config.getNode("recipes", "quantity-module").isVirtual()) {
			config.getNode("recipes", "quantity-module", "enable").setValue(true);
			config.getNode("recipes", "quantity-module", "id").setValue("quantity_module");
			config.getNode("recipes", "quantity-module", "row1").setValue(ItemTypes.GOLD_BLOCK.getId() + "," + ItemTypes.COMPARATOR.getId() + "," + ItemTypes.GOLD_BLOCK.getId());
			config.getNode("recipes", "quantity-module", "row2").setValue(ItemTypes.REDSTONE.getId() + "," + ItemTypes.DIAMOND.getId() + "," + ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "quantity-module", "row3").setValue(ItemTypes.GOLD_BLOCK.getId() + "," + ItemTypes.GOLD_BLOCK.getId() + "," + ItemTypes.GOLD_BLOCK.getId());
		}
		if (config.getNode("recipes", "entity-module").isVirtual()) {
			config.getNode("recipes", "entity-module", "enable").setValue(true);
			config.getNode("recipes", "entity-module", "id").setValue("entity_module");
			config.getNode("recipes", "entity-module", "row1").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "entity-module", "row2").setValue(ItemTypes.REDSTONE.getId() + "," + ItemTypes.REPEATER.getId() + "," + ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "entity-module", "row3").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.GOLD_INGOT.getId());
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
		if (config.getNode("settings", "sql", "database").isVirtual()) {
			config.getNode("settings", "sql", "database").setValue(Main.getPlugin().getId());
		}
		
		configManager.save();
	}
	
	public static void initRecipes() {
		ConfigurationNode recipes = ConfigManager.get(Main.getPlugin()).getConfig().getNode("recipes");
		ConfigurationNode settings = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings");
		
		if (recipes.getNode("quantity-module", "enable").getBoolean()) {
			RecipeManager.register(Resource.ID, recipes.getNode("quantity-module"), Items.getQuantityModule(settings.getNode("quantity-module-increment").getInt()));
		}
		if (recipes.getNode("speed-module", "enable").getBoolean()) {
			RecipeManager.register(Resource.ID, recipes.getNode("speed-module"), Items.getSpeedModule(settings.getNode("speed-module-increment").getInt()));
		}
		if (recipes.getNode("entity-module", "enable").getBoolean()) {
			RecipeManager.register(Resource.ID, recipes.getNode("entity-module"), Items.getEntityModule());
		}
		if (recipes.getNode("spawner", "enable").getBoolean()) {
			RecipeManager.register(Resource.ID, recipes.getNode("spawner"), Items.getSpawner());
		}
	}
}
