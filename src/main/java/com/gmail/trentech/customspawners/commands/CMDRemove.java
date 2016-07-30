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

public class CMDRemove implements CommandExecutor {

	public CMDRemove() {
		Help help = new Help("remove", "remove", " Remove spawner based on the name it was created");
		help.setSyntax(" /spawner remove <name>\n /cs r <name>");
		help.setExample(" /spawner remove MySpawner");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("name")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner remove <name>"));
			return CommandResult.empty();
		}
		String name = args.<String> getOne("name").get().toLowerCase();

		Optional<Spawner> optionalSpawner = Spawner.get(name);

		if (!optionalSpawner.isPresent()) {
			src.sendMessage(Text.of(TextColors.RED, name, " does not exist"));
			return CommandResult.empty();
		}
		Spawner spawner = optionalSpawner.get();
		spawner.remove();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Spawner removed"));

		return CommandResult.success();
	}
}
