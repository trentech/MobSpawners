package com.gmail.trentech.customspawners.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.customspawners.data.spawner.Spawner;
import com.gmail.trentech.customspawners.utils.Help;

public class CMDDisable implements CommandExecutor {

	public CMDDisable() {
		Help help = new Help("disable", "disable", " Disable spawner based on the name it was created");
		help.setPermission("customspawners.cmd.spawner.disable");
		help.setSyntax(" /spawner disable <name>\n /cs d <name>");
		help.setExample(" /spawner disable MySpawner");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String> getOne("name").get().toLowerCase();

		Optional<Spawner> optionalSpawner = Spawner.get(name);

		if (!optionalSpawner.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"), false);
		}
		Spawner spawner = optionalSpawner.get();

		spawner.setEnabled(false);
		spawner.update();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Spawner disabled"));

		return CommandResult.success();
	}
}
