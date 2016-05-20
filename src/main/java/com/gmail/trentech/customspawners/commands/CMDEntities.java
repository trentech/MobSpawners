package com.gmail.trentech.customspawners.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.customspawners.Main;
import com.gmail.trentech.customspawners.utils.Help;

public class CMDEntities implements CommandExecutor {

	public CMDEntities(){
		Help help = new Help("entities", "entities", " List all available entities");
		help.setSyntax(" /spawner entities\n /cs ent");
		help.setExample(" /spawner entities");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Entities")).build());
		
		List<Text> list = new ArrayList<>();
		
		for(EntityType entityType : Main.getGame().getRegistry().getAllOf(EntityType.class)) {
			if(!Living.class.isAssignableFrom(entityType.getEntityClass())){	
				continue;
			}
			
			if(entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER)) {
				continue;
			}
			list.add(Text.of(TextColors.GREEN, entityType.getId()));
		}
		
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}
}
