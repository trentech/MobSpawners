package com.gmail.trentech.customspawners.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.customspawners.utils.Help;

public class CommandManager {
	
	private CommandSpec cmdCreate = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.create")
		    .arguments()
		    .arguments(GenericArguments.string(Text.of("name")), GenericArguments.string(Text.of("entity,entity...")),
		    		GenericArguments.integer(Text.of("amount")), GenericArguments.integer(Text.of("time")),
		    		GenericArguments.integer(Text.of("radius")))
		    .executor(new CMDCreate())
		    .build();
	
	private CommandSpec cmdRemove = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.remove")
		    .arguments(GenericArguments.string(Text.of("name")))
		    .executor(new CMDRemove())
		    .build();
	
	private CommandSpec cmdEnable = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.enable")
		    .arguments(GenericArguments.string(Text.of("name")))
		    .executor(new CMDEnable())
		    .build();
	
	private CommandSpec cmdDisable = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner.disable")
		    .arguments(GenericArguments.string(Text.of("name")))
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
	
	private CommandSpec cmdHelp = CommandSpec.builder()
		    .description(Text.of(" I need help with Custom Spawners"))
		    .permission("customspawners.cmd.spawner")
		    .arguments(GenericArguments.choices(Text.of("command"), Help.all()))
		    .executor(new CMDHelp())
		    .build();
	
	public CommandSpec cmdSpawner = CommandSpec.builder()
		    .permission("customspawners.cmd.spawner")
		    .child(cmdCreate, "create", "c")
		    .child(cmdRemove, "remove", "r")
		    .child(cmdEnable, "enable", "e")
		    .child(cmdDisable, "disable", "d")
		    .child(cmdList, "list", "l")
		    .child(cmdEntities, "entities", "ent")
		    .child(cmdHelp, "help", "h")
		    .executor(new CMDSpawner())
		    .build();
}
