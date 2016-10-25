package com.gmail.trentech.mobspawners.utils;

import org.spongepowered.api.Sponge;

import com.gmail.trentech.helpme.help.Argument;
import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.helpme.help.Usage;

public class CommandHelp {

	public static void init() {
		if(Sponge.getPluginManager().isLoaded("helpme")) {
			
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
	}
}
