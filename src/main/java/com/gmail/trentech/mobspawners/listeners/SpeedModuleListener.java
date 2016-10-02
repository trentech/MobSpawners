package com.gmail.trentech.mobspawners.listeners;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.data.spawner.Spawner;

public class SpeedModuleListener {

	private static ConcurrentHashMap<UUID, Integer> cache = new ConcurrentHashMap<>();
	
	@Listener
	public void onClientConnectionEventJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
		checkItemInHand(player);
	}
	
	@Listener(order = Order.POST)
	public void onAffectSlotEvent(AffectSlotEvent event, @Root Player player) {
		Sponge.getScheduler().createTaskBuilder().async().delayTicks(3).execute(task -> {
			checkItemInHand(player);
		}).submit(Main.getPlugin());
	}
	
	@Listener
	public void onInteractBlockEventEventSecondary(InteractBlockEvent.Secondary event, @First Player player) {
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

		int speed = Integer.parseInt(lore.get(0).toPlain().replace("Speed: ", ""));

		spawner.setTime(spawner.getTime() - speed);
		spawner.update();

		player.getInventory().query(itemStack).poll(1);

		player.sendMessage(Text.of(TextColors.GREEN, "Speed module inserted"));
	}
	
	public static void checkItemInHand(Player player) {
		Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);

		if (optionalItemStack.isPresent()) {
			ItemStack itemStack = optionalItemStack.get();

			Optional<Text> optionalDisplayName = itemStack.get(Keys.DISPLAY_NAME);

			if (!optionalDisplayName.isPresent()) {
				return;
			}

			if (!optionalDisplayName.get().toPlain().equalsIgnoreCase("Speed Module")) {
				return;
			}

			List<Text> lore = itemStack.get(Keys.ITEM_LORE).get();

			int speed = Integer.parseInt(lore.get(0).toPlain().replace("Speed: ", ""));
			
			cache.put(player.getUniqueId(), speed);

			return;		
		}

		cache.remove(player.getUniqueId());
	}
}
