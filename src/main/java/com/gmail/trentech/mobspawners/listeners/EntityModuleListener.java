package com.gmail.trentech.mobspawners.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.data.entity.EntityData;
import com.gmail.trentech.mobspawners.data.spawner.Spawner;

public class EntityModuleListener {

	@Listener
	public void onInteractEntityEventPrimaryMainHand(InteractEntityEvent.Primary event, @Root Player player) {
		Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);

		if (!optionalItemStack.isPresent()) {
			return;
		}
		ItemStack itemStack = optionalItemStack.get();
		
		Optional<Text> optionalDisplayName = itemStack.get(Keys.DISPLAY_NAME);

		if (!optionalDisplayName.isPresent()) {
			return;
		}

		if (!optionalDisplayName.get().toPlain().equalsIgnoreCase("Entity Module")) {
			return;
		}

		event.setCancelled(true);
		
		Optional<EntityData> optionalEntityData = itemStack.get(EntityData.class);
		
		if(optionalEntityData.isPresent()) {
			return;
		}
		
		Entity entity = event.getTargetEntity();
		
		ItemStack copy = itemStack.copy();
		
		List<Text> lore = new ArrayList<>();

		lore.add(Text.of(TextColors.GREEN, "Entity: ", TextColors.WHITE, entity.getType().getTranslation().get()));
		
		copy.offer(Keys.ITEM_LORE, lore);

		copy.offer(new EntityData(entity.createArchetype()));

		player.getInventory().query(itemStack).set(copy);
		
		entity.remove();
	}
	
	@Listener
	public void onInteractBlockEventEventPrimary(InteractBlockEvent.Primary event, @First Player player) {
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

		if (!optionalDisplayName.get().toPlain().equalsIgnoreCase("Entity Module")) {
			return;
		}

		event.setCancelled(true);
		
		Optional<EntityData> optionalEntityData = itemStack.get(EntityData.class);
		
		if(!optionalEntityData.isPresent()) {
			return;
		}
		EntityData entityData = optionalEntityData.get();

		spawner.addEntity(entityData.entity().get());
		spawner.update();

		if(!player.gameMode().get().equals(GameModes.CREATIVE)) {
			player.getInventory().query(itemStack).poll(1);
		}

		player.sendMessage(Text.of(TextColors.GREEN, "Entity module inserted"));
	}
}
