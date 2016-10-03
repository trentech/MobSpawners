package com.gmail.trentech.mobspawners.commands.module;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.mobspawners.init.Items;
import com.gmail.trentech.mobspawners.utils.ConfigManager;
import com.gmail.trentech.mobspawners.utils.Help;

public class CMDSpeed implements CommandExecutor {

	public CMDSpeed() {
		Help help = new Help("m.speed", "speed", " Temporary command to create speed module");
		help.setPermission("mobspawners.cmd.spawner.module.speed");
		help.setSyntax(" /spawner module speed\n /ms m s");
		help.setExample(" /spawner module speed");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.DARK_RED, "Must be a player"));
		}
		Player player = (Player) src;

		ItemStack itemStack = Items.getSpeedModule(ConfigManager.get().getConfig().getNode("settings", "speed-module-increment").getInt());

		player.getInventory().offer(itemStack);

		return CommandResult.success();
	}
}
