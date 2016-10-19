package com.gmail.trentech.mobspawners.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.Help;

public class CMDModule implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(Sponge.getPluginManager().isLoaded("helpme")) {
			Help.executeList(src, Help.get("spawner module").get().getChildren());
			
			return CommandResult.success();
		}
		
		List<Text> list = new ArrayList<>();

		if (src.hasPermission("mobspawners.cmd.spawner.module.entity")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/mobspawners:spawner module entity")).append(Text.of(" /spawner module entity")).build());
		}
		if (src.hasPermission("mobspawners.cmd.spawner.module.speed")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/mobspawners:spawner module speed")).append(Text.of(" /spawner module speed")).build());
		}
		if (src.hasPermission("mobspawners.cmd.spawner.module.quantity")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/mobspawners:spawner module quantity")).append(Text.of(" /spawner module quantity")).build());
		}
		
		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

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
