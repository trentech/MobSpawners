package com.gmail.trentech.customspawners.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {
	
	private CommandSpec cmdCreate = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.create")
		    .arguments()
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("entity"))),
		    		GenericArguments.optional(GenericArguments.string(Text.of("amount"))), GenericArguments.optional(GenericArguments.string(Text.of("time"))),
		    		GenericArguments.optional(GenericArguments.string(Text.of("radius"))))
		    .executor(new CMDCreate())
		    .build();
	
	private CommandSpec cmdRemove = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.remove")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new CMDRemove())
		    .build();
	
	private CommandSpec cmdEnable = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.enable")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new CMDEnable())
		    .build();
	
	private CommandSpec cmdDisable = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.disable")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
		    .executor(new CMDDisable())
		    .build();
	
	private CommandSpec cmdList = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.list")
		    .executor(new CMDList())
		    .build();

	private CommandSpec cmdEntities = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.entities")
		    .executor(new CMDEntities())
		    .build();
	
	public CommandSpec cmdSpawner = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner")
		    .child(cmdCreate, "create", "c")
		    .child(cmdRemove, "remove", "r")
		    .child(cmdEnable, "enable", "e")
		    .child(cmdDisable, "disable", "d")
		    .child(cmdList, "list", "l")
		    .child(cmdEntities, "entities", "ent")
		    .executor(new CMDSpawner())
		    .build();
}
