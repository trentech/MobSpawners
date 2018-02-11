package com.gmail.trentech.mobspawners.init;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.data.manipulator.EntityModuleData;
import com.gmail.trentech.mobspawners.data.manipulator.SpawnerData;
import com.gmail.trentech.mobspawners.data.serializable.EntityModule;
import com.gmail.trentech.mobspawners.data.serializable.Spawner;
import com.gmail.trentech.pjc.core.ConfigManager;

public class Items {

	public static ItemStack QUANTITY_MODULE;
	public static ItemStack SPEED_MODULE;
	public static ItemStack ENTITY_MODULE;
	public static ItemStack SPAWNER;
	
	public static void init() {
		List<Enchantment> enchantments = new ArrayList<>();
		enchantments.add(Enchantment.builder().type(EnchantmentTypes.FEATHER_FALLING).level(1).build());
		
		ItemStack itemStack = ItemStack.of(ItemTypes.PAPER, 1);

		List<Text> lore = new ArrayList<>();

		lore.add(Text.of(TextColors.GREEN, "Quantity: ", TextColors.WHITE, ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "quantity-module-increment").getInt()));

		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Quantity Module"));
		itemStack.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
		itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);
		itemStack.offer(Keys.HIDE_ATTRIBUTES, true);
		itemStack.offer(Keys.ITEM_LORE, lore);
	//  itemStack.offer(new QuantityModuleData());

		QUANTITY_MODULE = itemStack;
		
		itemStack = ItemStack.of(ItemTypes.PAPER, 1);

		lore = new ArrayList<>();

		lore.add(Text.of(TextColors.GREEN, "Speed: ", TextColors.WHITE, TextColors.WHITE, ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "speed-module-increment").getInt()));

		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Speed Module"));
		itemStack.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
		itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);
		itemStack.offer(Keys.HIDE_ATTRIBUTES, true);
		itemStack.offer(Keys.ITEM_LORE, lore);
	//	itemStack.offer(new SpeedModuleData(new SpeedModule()));
		
		SPEED_MODULE = itemStack;
		
		itemStack = ItemStack.of(ItemTypes.PAPER, 1);

		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Entity Module"));
		itemStack.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
		itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);
		itemStack.offer(Keys.HIDE_ATTRIBUTES, true);
//		itemStack.offer(new EntityModuleData(new EntityModule()));
		
		ENTITY_MODULE = itemStack;
		
		itemStack = ItemStack.of(ItemTypes.STAINED_GLASS, 1);

		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Spawner"));
		itemStack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
		itemStack.offer(Keys.ITEM_ENCHANTMENTS, enchantments);
		itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);
		itemStack.offer(Keys.HIDE_ATTRIBUTES, true);
//		itemStack.offer(new SpawnerData(new Spawner()));
		
		SPAWNER = itemStack;
	}
	
	public static ItemStack getQuantityModule() {
		return QUANTITY_MODULE;
	}

	public static ItemStack getSpeedModule() {
		return SPEED_MODULE;
	}

	public static ItemStack getEntityModule() {
		return ENTITY_MODULE;
	}

	public static ItemStack getEntityModule(EntityArchetype entity) {
		ItemStack itemStack = getEntityModule();
		
		List<Text> lore = new ArrayList<>();

		lore.add(Text.of(TextColors.GREEN, "Entity: ", TextColors.WHITE, entity.getType().getTranslation().get()));
		
		itemStack.offer(Keys.ITEM_LORE, lore);

		itemStack.offer(new EntityModuleData(new EntityModule(entity)));
		
		return itemStack;
	}
	
	public static ItemStack getSpawner() {
		return SPAWNER;
	}
	
	public static ItemStack getSpawner(Spawner spawner) {
		ItemStack itemStack = getSpawner();

		List<Text> lore = new ArrayList<>();

		if (!spawner.getEntities().isEmpty()) {
			lore.add(Text.of(TextColors.GREEN, "Entities:"));

			for (EntityArchetype snapshot : spawner.getEntities()) {
				lore.add(Text.of(TextColors.YELLOW, "  - ", snapshot.getType().getTranslation().get()));
			}
		}

		lore.add(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, spawner.getTime(), " seconds"));
		lore.add(Text.of(TextColors.GREEN, "Quantity: ", TextColors.WHITE, spawner.getAmount()));

		itemStack.offer(new SpawnerData(spawner));

		return itemStack;
	}
}
