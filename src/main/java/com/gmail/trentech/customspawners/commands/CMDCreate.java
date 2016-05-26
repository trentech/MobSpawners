package com.gmail.trentech.customspawners.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.Main;
import com.gmail.trentech.customspawners.data.spawner.Spawner;
import com.gmail.trentech.customspawners.utils.Help;

public class CMDCreate implements CommandExecutor {

	public CMDCreate(){
		Help help = new Help("create", "create", " Use this command to create a spawner");
		help.setSyntax(" /spawner create <name> <entity,entity...> <amount> <time> <radius>\n /cs c <name> <entity,entity...> <amount> <time> <radius>");
		help.setExample(" /spawner create MySpawner ZOMBIE 3 10 20");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		if(!args.hasAny("name")) {
			src.sendMessage(Text.of(TextColors.RED, "Not enough arguments"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}
		String name = args.<String>getOne("name").get();

		if(!args.hasAny("entity")) {
			src.sendMessage(Text.of(TextColors.RED, "Not enough arguments"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}
		List<String> entities = Arrays.asList(args.<String>getOne("entity").get().split(","));
		
		if(!args.hasAny("amount")) {
			src.sendMessage(Text.of(TextColors.RED, "Not enough arguments"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}
		int amount;
		
		if(!args.hasAny("time")) {
			src.sendMessage(Text.of(TextColors.RED, "Not enough arguments"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}
		int time;
		
		if(!args.hasAny("radius")) {
			src.sendMessage(Text.of(TextColors.RED, "Not enough arguments"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}
		int radius;
		
		Optional<Spawner> optionalSpawner = Spawner.get(name);
		
		if(optionalSpawner.isPresent()) {
			src.sendMessage(Text.of(TextColors.RED, name, " already exists"));
			return CommandResult.empty();
		}

		for(String entityName : entities) {
			Optional<EntityType> optionalEntity = Main.getGame().getRegistry().getType(EntityType.class, entityName);
			
			if(!optionalEntity.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "<entity,entity...> Not a valid entity"));
				src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
				return CommandResult.empty();
			}
			EntityType entityType = optionalEntity.get();
			
			if(!Living.class.isAssignableFrom(entityType.getEntityClass())) {	
				src.sendMessage(Text.of(TextColors.RED, "<entity> Not a valid entity"));
				src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
				return CommandResult.empty();
			}
			
			if(entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER)) {
				src.sendMessage(Text.of(TextColors.RED, "<entity> Not a valid entity"));
				src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
				return CommandResult.empty();
			}
		}

		try{
			amount = Integer.parseInt(args.<String>getOne("amount").get());
		}catch(Exception e) {
			src.sendMessage(Text.of(TextColors.RED, "<amount> Not a valid number"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}
		
		if(amount <= 0) {
			src.sendMessage(Text.of(TextColors.RED, "<amount> Must be greater than 0"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}

		try{
			time = Integer.parseInt(args.<String>getOne("time").get());
		}catch(Exception e) {
			src.sendMessage(Text.of(TextColors.RED, "<time> Not a valid number"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}
		
		if(time <= 0) {
			src.sendMessage(Text.of(TextColors.RED, "<time> Must be greater than 0"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}

		try{
			radius = Integer.parseInt(args.<String>getOne("radius").get());
		}catch(Exception e) {
			src.sendMessage(Text.of(TextColors.RED, "<radius> Not a valid number"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}

		if(radius <= 0) {
			src.sendMessage(Text.of(TextColors.RED, "<radius> Must be greater than 0"));
			src.sendMessage(Text.of(TextColors.YELLOW, "/spawner create <name> <entity,entity...> <amount> <time> <radius>"));
			return CommandResult.empty();
		}
		
		Location<World> location = player.getLocation();
		
		String loc = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		new Spawner(entities, loc, amount, time, radius, true).create(name);
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Spawner created"));

		return CommandResult.success();
	}
}
