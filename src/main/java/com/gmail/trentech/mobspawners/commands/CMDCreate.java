package com.gmail.trentech.mobspawners.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.gmail.trentech.mobspawners.init.Items;
import com.gmail.trentech.mobspawners.listeners.SpawnerListener;
import com.gmail.trentech.mobspawners.utils.Help;

public class CMDCreate implements CommandExecutor {

	public CMDCreate() {
		new Help("sban create", "create", "Temporary command to create mob spawner", false)
			.setPermission("mobspawners.cmd.spawner.create")
			.setUsage("/spawner create <entity>\n /ms c <entity>")
			.setExample("/spawner create minecraft:zombie")
			.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.DARK_RED, "Must be a player"));
		}
		Player player = (Player) src;

		EntityType entity = args.<EntityType>getOne("entity").get();

		ItemStack itemStack = Items.getSpawner(new Spawner(entity));

		player.getInventory().offer(itemStack);

		SpawnerListener.checkItemInHand(player);

		return CommandResult.success();
	}
}
