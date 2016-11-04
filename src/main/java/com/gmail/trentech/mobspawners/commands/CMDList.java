package com.gmail.trentech.mobspawners.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;

public class CMDList implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		for (Entry<Location<World>, Spawner> entry : Spawner.all().entrySet()) {
			Location<World> location = entry.getKey();
			Spawner spawner = entry.getValue();

			list.add(Text.of(TextColors.YELLOW, "Location:", TextColors.GREEN, " w: ", TextColors.WHITE, location.getExtent().getName(), TextColors.GREEN, " x: ", TextColors.WHITE, location.getBlockX(), TextColors.GREEN, " y: ", TextColors.WHITE, location.getBlockY(), TextColors.GREEN, " z: ", TextColors.WHITE, location.getBlockZ()));

			if (spawner.isEnabled()) {
				list.add(Text.of(TextColors.YELLOW, "  Status: ", Text.of(TextColors.GREEN, "enabled")));
			} else {
				list.add(Text.of(TextColors.YELLOW, "  Status: ", Text.of(TextColors.GREEN, "enabled")));
			}

			list.add(Text.of(TextColors.YELLOW, "  Entities:"));
			
			for (EntityArchetype snapshot : spawner.getEntities()) {
				list.add(Text.of("  - ", snapshot.getType().getTranslation().get()));
			}
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Spawners")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}
}
