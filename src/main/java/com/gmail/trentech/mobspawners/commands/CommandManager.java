package com.gmail.trentech.mobspawners.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.mobspawners.utils.Help;

public class CommandManager {
	
	private CommandSpec cmdCreate = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner.create")
		    .arguments()
		    .arguments(GenericArguments.catalogedElement(Text.of("entity"), EntityType.class))
		    .executor(new CMDCreate())
		    .build();
	
	private CommandSpec cmdList = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner.list")
		    .executor(new CMDList())
		    .build();

	private CommandSpec cmdHelp = CommandSpec.builder()
		    .description(Text.of(" I need help with Custom Spawners"))
		    .permission("mobspawners.cmd.spawner")
		    .arguments(GenericArguments.choices(Text.of("command"), Help.all()))
		    .executor(new CMDHelp())
		    .build();
	
	public CommandSpec cmdSpawner = CommandSpec.builder()
		    .permission("mobspawners.cmd.spawner")
		    .child(cmdCreate, "create", "c")
		    .child(cmdList, "list", "l")
		    .child(cmdHelp, "help", "h")
		    .executor(new CMDSpawner())
		    .build();
}
