package com.gmail.trentech.mobspawners.commands.module;

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

import com.gmail.trentech.mobspawners.init.Items;
import com.gmail.trentech.mobspawners.utils.Help;

public class CMDEntity implements CommandExecutor {

	public CMDEntity() {
		Help help = new Help("m.entity", "entity", " Temporary command to create mob module");
		help.setPermission("mobspawners.cmd.spawner.module.entity");
		help.setSyntax(" /spawner module entity <entity>\n /ms m e <entity>");
		help.setExample(" /spawner module entity minecraft:creeper");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.DARK_RED, "Must be a player"));
		}
		Player player = (Player) src;

		EntityType entity = args.<EntityType>getOne("entity").get();

		ItemStack itemStack = Items.getEntityModule(entity);

		player.getInventory().offer(itemStack);

		return CommandResult.success();
	}
}
