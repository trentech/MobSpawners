package com.gmail.trentech.mobspawners.listeners;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;

public class SpeedModuleListener {

	@Listener
	public void onInteractBlockEventEventSecondary(InteractBlockEvent.Primary event, @First Player player) {
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();

		if (!optionalLocation.isPresent()) {
			return;
		}
		Location<World> location = optionalLocation.get();

		Optional<Spawner> optionalSpawner = Spawner.get(location);

		if (!optionalSpawner.isPresent()) {
			return;
		}
		Spawner spawner = optionalSpawner.get();

		Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);

		if (!optionalItemStack.isPresent()) {
			return;
		}
		ItemStack itemStack = optionalItemStack.get();

		Optional<Text> optionalDisplayName = itemStack.get(Keys.DISPLAY_NAME);

		if (!optionalDisplayName.isPresent()) {
			return;
		}

		if (!optionalDisplayName.get().toPlain().equalsIgnoreCase("Speed Module")) {
			return;
		}

		List<Text> lore = itemStack.get(Keys.ITEM_LORE).get();

		int speed = spawner.getTime() - Integer.parseInt(lore.get(0).toPlain().replace("Speed: ", ""));

		if(speed >= 5) {
			spawner.setTime(speed);
			spawner.update();

			if(!player.gameMode().get().equals(GameModes.CREATIVE)) {
				player.getInventory().query(itemStack).poll(1);
			}

			player.sendMessage(Text.of(TextColors.GREEN, "Speed module inserted"));
			
			event.setCancelled(true);
		}
	}

}
