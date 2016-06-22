package com.gmail.trentech.customspawners.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.Main;
import com.gmail.trentech.customspawners.data.spawner.Spawner;
import com.gmail.trentech.customspawners.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList() {
		Help help = new Help("list", "list", " List all spawners by name");
		help.setSyntax(" /spawner list\n /cs l");
		help.setExample(" /spawner list");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Entities")).build());

		List<Text> list = new ArrayList<>();

		for (Entry<String, Spawner> entry : Spawner.all().entrySet()) {
			String name = entry.getKey();
			Spawner spawner = entry.getValue();

			Location<World> location = spawner.getLocation();

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

			list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, name, "\n  ", enable, loc, TextColors.YELLOW, "Amount: ", TextColors.WHITE, spawner.getAmount(), "\n  ", TextColors.YELLOW, "Time: ", TextColors.WHITE, spawner.getTime(), "\n  ", TextColors.YELLOW, "Radius: ", TextColors.WHITE, spawner.getRadius(), "\n  ", ents));
		}

		pages.contents(list);

		pages.sendTo(src);

		return CommandResult.success();
	}
}
