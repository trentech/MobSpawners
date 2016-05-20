package com.gmail.trentech.customspawners.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.customspawners.Main;
import com.gmail.trentech.customspawners.utils.Help;

public class CMDSpawner implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());
		
		List<Text> list = new ArrayList<>();

		if(src.hasPermission("customspawners.cmd.spawner.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands ")))
					.onClick(TextActions.executeCallback(Help.getHelp("create"))).append(Text.of(" /spawner create")).build());
		}
		if(src.hasPermission("customspawners.cmd.spawner.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands ")))
					.onClick(TextActions.executeCallback(Help.getHelp("remove"))).append(Text.of(" /spawner remove")).build());
		}
		if(src.hasPermission("customspawners.cmd.spawner.enable")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands ")))
					.onClick(TextActions.executeCallback(Help.getHelp("enable"))).append(Text.of(" /spawner enable")).build());
		}
		if(src.hasPermission("customspawners.cmd.spawner.disable")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands ")))
					.onClick(TextActions.executeCallback(Help.getHelp("disable"))).append(Text.of(" /spawner disable")).build());
		}
		if(src.hasPermission("customspawners.cmd.spawner.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands ")))
					.onClick(TextActions.executeCallback(Help.getHelp("list"))).append(Text.of(" /spawner list")).build());
		}
		if(src.hasPermission("customspawners.cmd.spawner.entities")) {
			list.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands ")))
					.onClick(TextActions.executeCallback(Help.getHelp("entities"))).append(Text.of(" /spawner entities")).build());
		}
		pages.contents(list);
		
		pages.sendTo(src);
		
		return CommandResult.success();
	}
}
