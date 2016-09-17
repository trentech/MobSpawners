package com.gmail.trentech.customspawners.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.data.spawner.Spawner;
import com.gmail.trentech.customspawners.utils.Help;

public class CMDCreate implements CommandExecutor {

	public CMDCreate() {
		Help help = new Help("create", "create", " Use this command to create a spawner");
		help.setPermission("customspawners.cmd.spawner.create");
		help.setSyntax(" /spawner create <name> <entity,entity...> <amount> <time> <radius>\n /cs c <name> <entity,entity...> <amount> <time> <radius>");
		help.setExample(" /spawner create MySpawner ZOMBIE 3 10 20");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.DARK_RED, "Must be a player"));
		}
		Player player = (Player) src;

		String name = args.<String> getOne("name").get().toLowerCase();

		String[] ent = args.<String> getOne("entity,entity...").get().split(",");

		int amount = args.<Integer> getOne("amount").get();
		
		if (amount <= 0) {
			throw new CommandException(Text.of(TextColors.RED, "<amount> Must be greater than 0"), false);
		}
		
		int time = args.<Integer> getOne("time").get();
		
		if (time <= 0) {
			throw new CommandException(Text.of(TextColors.RED, "<time> Must be greater than 0"), false);
		}
		
		int radius = args.<Integer> getOne("radius").get();

		if (radius <= 0) {
			throw new CommandException(Text.of(TextColors.RED, "<radius> Must be greater than 0"), false);
		}
		
		Optional<Spawner> optionalSpawner = Spawner.get(name);

		if (optionalSpawner.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " already exists"), false);
		}

		List<EntityType> entities = new ArrayList<>();

		for (String entityName : ent) {
			Optional<EntityType> optionalEntity = Sponge.getRegistry().getType(EntityType.class, entityName);

			if (!optionalEntity.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, "<entity,entity...> Not a valid entity"), false);
			}
			EntityType entityType = optionalEntity.get();

			if (!Living.class.isAssignableFrom(entityType.getEntityClass())) {
				throw new CommandException(Text.of(TextColors.RED, "<entity,entity...> Not a valid entity"), false);
			}

			if (entityType.equals(EntityTypes.ARMOR_STAND) || entityType.equals(EntityTypes.HUMAN) || entityType.equals(EntityTypes.PLAYER)) {
				throw new CommandException(Text.of(TextColors.RED, "<entity,entity...> Not a valid entity"), false);
			}

			entities.add(entityType);
		}

		BlockRay<World> blockRay = BlockRay.from(player).blockLimit(16).filter(BlockRay.onlyAirFilter()).build();
		
		Optional<BlockRayHit<World>> optionalHit = blockRay.end();
		
		if(!optionalHit.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "Must be looking at a block"), false);
		}
		Location<World> location = optionalHit.get().getLocation();
		
		new Spawner(name, entities, location, amount, time, radius, true).create();

		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Spawner created"));

		return CommandResult.success();
	}
}
