package com.gmail.trentech.mobspawners.listeners;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.data.manipulator.EntityModuleData;
import com.gmail.trentech.mobspawners.data.serializable.EntityModule;
import com.gmail.trentech.mobspawners.init.Items;

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
		
		Optional<EntityModuleData> optionalEntityModuleData = itemStack.get(EntityModuleData.class);
		
		if(optionalEntityModuleData.isPresent()) {
			return;
		}
		
		Entity entity = event.getTargetEntity();

		player.getInventory().query(QueryOperationTypes.ITEM_STACK_EXACT.of(itemStack)).poll(1);
		
		ItemStack copy = Items.getEntityModule(entity.createArchetype());

		PlayerInventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(PlayerInventory.class));
		
		if(!player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
			player.setItemInHand(HandTypes.MAIN_HAND, copy);
		} else {
			if(!inv.getHotbar().offer(copy).getType().equals(Type.SUCCESS)) {
				System.out.println("FAILED");
				if(!inv.getMainGrid().offer(copy).getType().equals(Type.SUCCESS)) {
					System.out.println("FAILED");
					Location<World> location = player.getLocation();
					
					Item item = (Item) location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
					item.offer(Keys.REPRESENTED_ITEM, copy.createSnapshot());
					
					location.getExtent().spawnEntity(item);
				}
			}
		}

		entity.remove();
	}
	
	@Listener
	public void onInteractEntityEventSecondary(InteractBlockEvent.Secondary event, @Root Player player) {
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

		Optional<EntityModuleData> optionalEntityModuleData = itemStack.get(EntityModuleData.class);
		
		if(!optionalEntityModuleData.isPresent()) {
			return;
		}
		EntityModule entityModule = optionalEntityModuleData.get().entityModule().get();

		Optional<EntityArchetype> optionalEntity = entityModule.getEntity();
		
		if(!optionalEntity.isPresent()) {
			return;
		}
		
		BlockRay<World> blockRay = BlockRay.from(player).distanceLimit(5).stopFilter(BlockRay.onlyAirFilter()).build();

		Optional<BlockRayHit<World>> optionalHit = blockRay.end();

		if (optionalHit.isPresent()) {
			Location<World> location = optionalHit.get().getLocation();
			
			optionalEntity.get().apply(location);

			player.getInventory().query(QueryOperationTypes.ITEM_STACK_EXACT.of(itemStack)).poll(1);
			player.setItemInHand(HandTypes.MAIN_HAND, Items.getEntityModule());
		}
	}
}
