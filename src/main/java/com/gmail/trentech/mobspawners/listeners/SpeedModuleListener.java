package com.gmail.trentech.mobspawners.listeners;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.CraftItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.mobspawners.data.manipulator.SpeedModuleData;

public class SpeedModuleListener {

	@Listener
	public void onCraftItemEvent(CraftItemEvent.Preview event) {
		Optional<ItemStack> peek = event.getCraftingInventory().getResult().peek();

		if(peek.isPresent()) {
			ItemStack itemStack = peek.get();
			
			Optional<Text> optionalDisplayName = itemStack.get(Keys.DISPLAY_NAME);

			if (!optionalDisplayName.isPresent()) {
				return;
			}

			if (!optionalDisplayName.get().toPlain().equalsIgnoreCase("Speed Module")) {
				return;
			}

			itemStack.offer(new SpeedModuleData());
			
			CraftingOutput output = event.getCraftingInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(CraftingOutput.class));

			System.out.println(output.set(itemStack).getType().name());
		}
	}
}
