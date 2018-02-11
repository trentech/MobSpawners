package com.gmail.trentech.mobspawners.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.data.manipulator.EntityModuleData;
import com.gmail.trentech.mobspawners.data.serializable.EntityModule;
import com.gmail.trentech.mobspawners.data.serializable.Spawner;
import com.gmail.trentech.mobspawners.init.Items;
import com.gmail.trentech.pjc.core.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public abstract class SpawnerInventoryHandler {

	public static class Open extends SpawnerInventoryHandler implements Consumer<InteractInventoryEvent.Open> {
		
		private Spawner spawner;
		
		public Open(Spawner spawner) {
			this.spawner = spawner;
		}

		@Override
		public void accept(InteractInventoryEvent.Open event) {
			ConfigurationNode settings = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings");
			
			int quantityStart = settings.getNode("spawn-amount").getInt();
			
			if(spawner.getAmount() > quantityStart) {
				int quantityIncrement = settings.getNode("quantity-module-increment").getInt();			
				
				ItemStack quantityModule = Items.getQuantityModule();
				quantityModule.setQuantity((spawner.getAmount() - quantityStart) / quantityIncrement);
				event.getTargetInventory().offer(quantityModule);
			}

			int speedStart = settings.getNode("time").getInt();

			if(spawner.getTime() < speedStart) {
				int speedIncrement = settings.getNode("speed-module-increment").getInt();

				ItemStack speedModule = Items.getSpeedModule();
				speedModule.setQuantity((speedStart - spawner.getTime()) / speedIncrement);
				
				event.getTargetInventory().offer(speedModule);
			}
			
			for(EntityArchetype entity : spawner.getEntities()) {
				ItemStack entityModule = Items.getEntityModule(entity);
				event.getTargetInventory().offer(entityModule);
			}
		}
	}
	
	public static class Close extends SpawnerInventoryHandler implements Consumer<InteractInventoryEvent.Close> {
		
		private Spawner spawner;
		
		public Close(Spawner spawner) {
			this.spawner = spawner;
		}

		@Override
		public void accept(InteractInventoryEvent.Close event) {
			ConfigurationNode settings = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings");
			
			List<EntityArchetype> list = new ArrayList<>();
			int amount = settings.getNode("spawn-amount").getInt();
			int time = settings.getNode("time").getInt();
			
			int i = 0;
			for(Inventory slot : event.getTargetInventory().slots()) {
				i++;
				
				if(i > 9) {
					break;
				}

				Optional<ItemStack> optionalItemStack = slot.peek();
				
				if(!optionalItemStack.isPresent()) {
					continue;
				}
				ItemStack itemStack = optionalItemStack.get();

				Optional<Text> optionalDisplayName = itemStack.get(Keys.DISPLAY_NAME);

				if (!optionalDisplayName.isPresent()) {
					continue;
				}

				if (optionalDisplayName.get().toPlain().equalsIgnoreCase("Quantity Module")) {
					amount = amount + (settings.getNode("quantity-module-increment").getInt() * itemStack.getQuantity());
					continue;
				}
				
				if (optionalDisplayName.get().toPlain().equalsIgnoreCase("Speed Module")) {
					time = time - (settings.getNode("speed-module-increment").getInt() * itemStack.getQuantity());
					continue;
				}
				
				if (optionalDisplayName.get().toPlain().equalsIgnoreCase("Entity Module")) {
					Optional<EntityModuleData> optionalEntityModuleData = itemStack.get(EntityModuleData.class);
					
					if(!optionalEntityModuleData.isPresent()) {
						continue;
					}
					EntityModule entityModule = optionalEntityModuleData.get().entityModule().get();

					Optional<EntityArchetype> optionalEntity = entityModule.getEntity();
					
					if(!optionalEntity.isPresent()) {
						continue;
					}
					
					list.add(optionalEntity.get());
					continue;
				}
			}
			
			spawner.setAmount(amount);
			spawner.setTime(time);
			spawner.setEntities(list);
			spawner.update();
		}
	}
}
