package com.gmail.trentech.mobspawners.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.gmail.trentech.pjc.help.Help;

public class CMDSpawner implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help.executeList(src, Help.get("spawner").get().getChildren());

		return CommandResult.success();
	}
}
