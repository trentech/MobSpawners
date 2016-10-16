package com.gmail.trentech.mobspawners.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.gmail.trentech.mobspawners.utils.Help;

public class CMDHelp implements CommandExecutor {

	public CMDHelp() {
		new Help("spawner help", "help", "Get help with all commands in MobSpawners", false)
			.setPermission("mobspawners.cmd.spawner")
			.setUsage("/spawner help <rawCommand>")
			.setExample("/spawner help spawner create")
			.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = args.<Help>getOne("rawCommand").get();
		help.execute(src);

		return CommandResult.success();
	}
}
