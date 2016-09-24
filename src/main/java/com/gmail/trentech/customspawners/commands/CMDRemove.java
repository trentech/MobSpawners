package com.gmail.trentech.customspawners.commands;

import java.util.Optional;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.Main;
import com.gmail.trentech.customspawners.data.spawner.Spawner;
import com.gmail.trentech.customspawners.utils.Help;

public class CMDRemove implements CommandExecutor {

	public CMDRemove() {
		Help help = new Help("remove", "remove", " Remove spawner based on the name it was created");
		help.setPermission("customspawners.cmd.spawner.remove");
		help.setSyntax(" /spawner remove <name>\n /cs r <name>");
		help.setExample(" /spawner remove MySpawner");
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
		
		Location<World> location = spawner.getLocation();
		
		if(location.setBlock(BlockTypes.AIR.getDefaultState(), Cause.source(Main.getPlugin()).named(NamedCause.simulated(src)).build())) {
			spawner.remove();

			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Spawner removed"));
			
			return CommandResult.success();
		}

		throw new CommandException(Text.of(TextColors.RED, "Could not remove spawner"), false);
	}
}
