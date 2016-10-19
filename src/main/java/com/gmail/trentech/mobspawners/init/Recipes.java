package com.gmail.trentech.mobspawners.init;

import java.util.Map.Entry;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.ShapedRecipe;
import org.spongepowered.api.item.recipe.ShapedRecipe.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.item.recipe.ShapelessRecipe;

import com.flowpowered.math.vector.Vector2i;
import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.gmail.trentech.mobspawners.data.spawner.SpawnerData;
import com.gmail.trentech.mobspawners.utils.ConfigManager;
import com.gmail.trentech.mobspawners.utils.InvalidItemTypeException;

import ninja.leaping.configurate.ConfigurationNode;

public class Recipes {

	public static void register() {
		try {
			ConfigurationNode recipesConfig = ConfigManager.get("recipes").getConfig();

			for (Entry<Object, ? extends ConfigurationNode> entry : recipesConfig.getChildrenMap().entrySet()) {
				ConfigurationNode child = entry.getValue();

				String key = child.getKey().toString();

				Optional<EntityType> optionalEntityType = Sponge.getRegistry().getType(EntityType.class, key);

				if (optionalEntityType.isPresent()) {
					EntityType entityType = optionalEntityType.get();

					Main.instance().getLog().info("Registering spawner and module recipes for " + entityType.getId());
					Sponge.getRegistry().getRecipeRegistry().register(getShapedRecipe(child.getNode("spawner"), Items.getSpawner(new Spawner(entityType))));
					Sponge.getRegistry().getRecipeRegistry().register(getShapedRecipe(child.getNode("module"), Items.getEntityModule(entityType)));
				}
			}

			ConfigurationNode config = ConfigManager.get().getConfig();

			Main.instance().getLog().info("Registering quantity module recipe");
			Sponge.getRegistry().getRecipeRegistry().register(getShapedRecipe(recipesConfig.getNode("quantity-module"), Items.getQuantityModule(config.getNode("settings", "quantity-module-increment").getInt())));
			Main.instance().getLog().info("Registering speed module recipe");
			Sponge.getRegistry().getRecipeRegistry().register(getShapedRecipe(recipesConfig.getNode("speed-module"), Items.getSpeedModule(config.getNode("settings", "speed-module-increment").getInt())));
		} catch (InvalidItemTypeException e) {
			e.printStackTrace();
		}

	}

	public static void remove() {
		try {
			ConfigurationNode recipesConfig = ConfigManager.get("recipes").getConfig();

			for (Entry<Object, ? extends ConfigurationNode> entry : recipesConfig.getChildrenMap().entrySet()) {
				ConfigurationNode child = entry.getValue();

				String key = child.getKey().toString();

				Optional<EntityType> optionalEntityType = Sponge.getRegistry().getType(EntityType.class, key);

				if (optionalEntityType.isPresent()) {
					EntityType entityType = optionalEntityType.get();

					Main.instance().getLog().info("Registering spawner and module recipes for " + entityType.getId());
					Sponge.getRegistry().getRecipeRegistry().remove(getShapedRecipe(child.getNode("spawner"), Items.getSpawner(new Spawner(entityType))));
					Sponge.getRegistry().getRecipeRegistry().remove(getShapedRecipe(child.getNode("module"), Items.getEntityModule(entityType)));
				}
			}

			ConfigurationNode config = ConfigManager.get().getConfig();

			Main.instance().getLog().info("Registering quantity module recipe");
			Sponge.getRegistry().getRecipeRegistry().remove(getShapedRecipe(recipesConfig.getNode("quantity-module"), Items.getQuantityModule(config.getNode("settings", "quantity-module-increment").getInt())));
			Main.instance().getLog().info("Registering speed module recipe");
			Sponge.getRegistry().getRecipeRegistry().remove(getShapedRecipe(recipesConfig.getNode("speed-module"), Items.getSpeedModule(config.getNode("settings", "speed-module-increment").getInt())));
		} catch (InvalidItemTypeException e) {
			e.printStackTrace();
		}

	}
	
	private static ShapedRecipe getShapedRecipe(ConfigurationNode recipe, ItemStack result) throws InvalidItemTypeException {
		Builder builder = Sponge.getRegistry().createBuilder(ShapedRecipe.Builder.class);

		for (Entry<Object, ? extends ConfigurationNode> entry : recipe.getChildrenMap().entrySet()) {
			ConfigurationNode child = entry.getValue();

			String key = child.getKey().toString();
			String itemId = child.getString();
			String[] args = itemId.split(":");

			Optional<ItemType> optionalItemType = Sponge.getRegistry().getType(ItemType.class, itemId);

			if (optionalItemType.isPresent()) {
				ItemStack itemStack = ItemStack.builder().itemType(optionalItemType.get()).build();

				if (args.length == 3) {
					DataContainer container = itemStack.toContainer();
					DataQuery query = DataQuery.of('/', "UnsafeDamage");
					container.set(query, Integer.parseInt(args[2]));
					itemStack.setRawData(container);
				}

				String[] grid = key.split("x");

				builder.ingredient(new Vector2i(Integer.parseInt(grid[0]), Integer.parseInt(grid[1])), itemStack);
			} else {
				throw new InvalidItemTypeException("ItemType in config.conf at " + key + " is invalid");
			}
		}

		return builder.addResult(result).build();
	}
	
	@SuppressWarnings("unused")
	private static ShapelessRecipe getShapelessRecipe(ItemStack spawnerStack, ItemStack moduleStack) {
		Optional<SpawnerData> optionalSpawnerData = spawnerStack.get(SpawnerData.class);
		
		if(!optionalSpawnerData.isPresent()) {
			return null;
		}
		SpawnerData spawnerData = optionalSpawnerData.get();
		Spawner spawner = spawnerData.spawner().get();
		
		Optional<Text> optionalDisplayName = moduleStack.get(Keys.DISPLAY_NAME);

		if (!optionalDisplayName.isPresent()) {
			return null;
		}
		String moduleType = optionalDisplayName.get().toPlain();
		
		if (moduleType.equalsIgnoreCase("Quantity Module")) {
			List<Text> lore = moduleStack.get(Keys.ITEM_LORE).get();

			int quantity = spawner.getAmount() + Integer.parseInt(lore.get(0).toPlain().replace("Quantity: ", ""));
			
			spawner.setAmount(quantity);
		} else if(moduleType.equalsIgnoreCase("Speed Module")) {
			List<Text> lore = moduleStack.get(Keys.ITEM_LORE).get();

			int speed = spawner.getTime() - Integer.parseInt(lore.get(0).toPlain().replace("Speed: ", ""));
			
			spawner.setTime(speed);
		} else if (moduleType.equalsIgnoreCase("Entity Module")) {
			List<Text> lore = moduleStack.get(Keys.ITEM_LORE).get();

			String entityId = lore.get(0).toPlain().replace("Entity: ", "");
			
			Optional<EntityType> optionalEntity = Sponge.getGame().getRegistry().getType(EntityType.class, entityId);

			spawner.addEntity(optionalEntity.get());
		} else {
			return null;
		}
		
		ShapelessRecipe.Builder builder = ShapelessRecipe.builder().addIngredient(spawnerStack).addIngredient(moduleStack);
		
		spawnerData.spawner().set(spawner);
		
		spawnerStack.offer(spawnerData);
		
		return builder.addResult(spawnerStack).build();
	}
}
