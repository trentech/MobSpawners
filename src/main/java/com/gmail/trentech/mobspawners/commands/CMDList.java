package com.gmail.trentech.mobspawners.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.gmail.trentech.mobspawners.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList() {
		Help help = new Help("s.list", "list", " List all spawners and their locations");
		help.setPermission("mobspawners.cmd.spawner.list");
		help.setSyntax(" /spawner list\n /ms l");
		help.setExample(" /spawner list");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		for (Entry<Location<World>, Spawner> entry : Spawner.all().entrySet()) {
			Location<World> location = entry.getKey();
			Spawner spawner = entry.getValue();

			Text loc = Text.of(TextColors.YELLOW, "Location:", TextColors.GREEN, " w: ", TextColors.WHITE, location.getExtent().getName(), TextColors.GREEN, " x: ", TextColors.WHITE, location.getBlockX(), TextColors.GREEN, " y: ", TextColors.WHITE, location.getBlockY(), TextColors.GREEN, " z: ", TextColors.WHITE, location.getBlockZ(), "\n  ");

			Text enable = Text.of(TextColors.YELLOW, "Status: ");
			if (spawner.isEnabled()) {
				enable = Text.join(enable, Text.of(TextColors.GREEN, "enabled\n  "));
			} else {
				enable = Text.join(enable, Text.of(TextColors.RED, "disabled\n  "));
			}

			Text ents = Text.of(TextColors.YELLOW, "Entities: \n", TextColors.WHITE);
			for (EntityType entityType : spawner.getEntities()) {
				ents = Text.join(ents, Text.of("  - ", entityType.getId(), "\n"));
			}

			list.add(Text.of(loc, enable, ents));
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
