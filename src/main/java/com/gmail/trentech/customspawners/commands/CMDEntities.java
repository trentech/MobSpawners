package com.gmail.trentech.customspawners.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.customspawners.utils.Help;

public class CMDEntities implements CommandExecutor {

	public CMDEntities() {
		Help help = new Help("entities", "entities", " List all available entities");
		help.setSyntax(" /spawner entities\n /cs ent");
		help.setExample(" /spawner entities");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		for (EntityType entityType : Sponge.getRegistry().getAllOf(EntityType.class)) {
			if (!Living.class.isAssignableFrom(entityType.getEntityClass())) {
				continue;
			}

			if (entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER)) {
				continue;
			}
			list.add(Text.of(TextColors.GREEN, entityType.getId()));
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Entities")).build());

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
