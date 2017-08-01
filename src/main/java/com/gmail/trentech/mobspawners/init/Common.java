package com.gmail.trentech.mobspawners.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.spongepowered.api.item.ItemTypes;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.pjc.help.Argument;
import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjc.help.Usage;

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
				.setPermission("mobspawners.cmd.spawner.module")
				.addChild(spawnerList)
				.addChild(spawnerCreate)
				.addChild(spawnerModule);
		
		Help.register(spawner);
	}
	
	public static void initConfig() {
		ConfigManager configManager = ConfigManager.init(Main.getPlugin());
		CommentedConfigurationNode config = configManager.getConfig();

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
		if (config.getNode("settings", "sql", "database").isVirtual()) {
			config.getNode("settings", "sql", "database").setValue(Main.getPlugin().getId());
		}
		
		configManager.save();
	}
	
	public static void initRecipeManager() {
//		ConfigurationNode config = ConfigManager.get(Main.getPlugin()).getConfig().getNode("recipes");
//
//		try {
//			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("spawner"), Items.getSpawner(new Spawner())));
//			
//			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("quantity-module"), Items.getQuantityModule(config.getNode("settings", "quantity-module-increment").getInt())));
//
//			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("speed-module"), Items.getSpeedModule(config.getNode("settings", "speed-module-increment").getInt())));
//		} catch (InvalidItemTypeException e) {
//			e.printStackTrace();
//		}
	}
}
