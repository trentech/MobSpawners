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
import com.gmail.trentech.mobspawners.listeners.QuantityModuleListener;
import com.gmail.trentech.mobspawners.utils.ConfigManager;
import com.gmail.trentech.mobspawners.utils.Help;

public class CMDQuantity implements CommandExecutor {

	public CMDQuantity() {
		Help help = new Help("m.quantity", "quantity", " Temporary command to create quantity module");
		help.setPermission("mobspawners.cmd.spawner.module.quantity");
		help.setSyntax(" /spawner module quantity\n /ms m q");
		help.setExample(" /spawner module quantity");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.DARK_RED, "Must be a player"));
		}
		Player player = (Player) src;

		ItemStack itemStack = Items.getQuantityModule(ConfigManager.get().getConfig().getNode("settings", "quantity_module_increment").getInt());

		player.getInventory().offer(itemStack);

		QuantityModuleListener.checkItemInHand(player);

		return CommandResult.success();
	}
}
