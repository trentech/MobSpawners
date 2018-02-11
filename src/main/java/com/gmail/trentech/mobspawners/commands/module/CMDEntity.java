package com.gmail.trentech.mobspawners.commands.module;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.mobspawners.init.Items;

public class CMDEntity implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.DARK_RED, "Must be a player"));
		}
		Player player = (Player) src;

		ItemStack itemStack = Items.getEntityModule();

		PlayerInventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(PlayerInventory.class));
		
		if(!inv.getHotbar().offer(itemStack).getType().equals(Type.SUCCESS)) {
			if(!inv.getMainGrid().offer(itemStack).getType().equals(Type.SUCCESS)) {
				src.sendMessage(Text.of(TextColors.RED, "Your inventory does not have enough space"));
			}
		}

		return CommandResult.success();
	}
}
