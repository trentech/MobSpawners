package com.gmail.trentech.mobspawners.init;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.gmail.trentech.mobspawners.data.spawner.SpawnerData;

public class Items {

	public static ItemStack getQuantityModule(int quantity) {
		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.PAPER).build();

		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Quantity Module"));

		List<Text> lore = new ArrayList<>();

		lore.add(Text.of(TextColors.GREEN, "Quantity: ", TextColors.WHITE, quantity));

		List<ItemEnchantment> enchantments = new ArrayList<>();
		enchantments.add(new ItemEnchantment(Enchantments.FLAME, 1));

		itemStack.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
		itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);

		itemStack.offer(Keys.ITEM_LORE, lore);

		return itemStack;
	}

	public static ItemStack getSpeedModule(int seconds) {
		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.PAPER).build();

		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Speed Module"));

		List<Text> lore = new ArrayList<>();

		lore.add(Text.of(TextColors.GREEN, "Speed: ", TextColors.WHITE, seconds));

		List<ItemEnchantment> enchantments = new ArrayList<>();
		enchantments.add(new ItemEnchantment(Enchantments.FLAME, 1));

		itemStack.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
		itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);

		itemStack.offer(Keys.ITEM_LORE, lore);

		return itemStack;
	}

	public static ItemStack getEntityModule() {
		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.PAPER).build();

		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Entity Module"));

		List<ItemEnchantment> enchantments = new ArrayList<>();
		enchantments.add(new ItemEnchantment(Enchantments.FLAME, 1));

		itemStack.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
		itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);

		return itemStack;
	}

	public static ItemStack getSpawner(Spawner spawner) {
		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS).build();
		
		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Spawner"));
		itemStack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
		
		itemStack.offer(new SpawnerData(spawner));
		List<Text> lore = new ArrayList<>();

		if (!spawner.getEntities().isEmpty()) {
			lore.add(Text.of(TextColors.GREEN, "Entities:"));

			for (EntityArchetype snapshot : spawner.getEntities()) {
				lore.add(Text.of(TextColors.YELLOW, "  - ", snapshot.getType().getTranslation().get()));
			}
		}

		lore.add(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, spawner.getTime(), " seconds"));
		lore.add(Text.of(TextColors.GREEN, "Quantity: ", TextColors.WHITE, spawner.getAmount()));

		List<ItemEnchantment> enchantments = new ArrayList<>();
		enchantments.add(new ItemEnchantment(Enchantments.FLAME, 1));

		itemStack.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
		itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);

		itemStack.offer(Keys.ITEM_LORE, lore);

		return itemStack;
	}
}
