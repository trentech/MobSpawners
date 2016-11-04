package com.gmail.trentech.mobspawners.init;

import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.ShapedRecipe;
import org.spongepowered.api.item.recipe.ShapedRecipe.Builder;

import com.flowpowered.math.vector.Vector2i;
import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.gmail.trentech.mobspawners.utils.ConfigManager;
import com.gmail.trentech.mobspawners.utils.InvalidItemTypeException;

import ninja.leaping.configurate.ConfigurationNode;

public class Recipes {

	public static void register() throws InvalidItemTypeException {
		ConfigurationNode config = ConfigManager.get().getConfig();
		ConfigurationNode recipes = config.getNode("recipes");

		Main.instance().getLog().info("Registering spawner recipe");		
		Sponge.getRegistry().getRecipeRegistry().register(getShapedRecipe(recipes.getNode("spawner"), Items.getSpawner(new Spawner())));
		
		Main.instance().getLog().info("Registering quantity module recipe");
		Sponge.getRegistry().getRecipeRegistry().register(getShapedRecipe(recipes.getNode("quantity-module"), Items.getQuantityModule(config.getNode("settings", "quantity-module-increment").getInt())));
		
		Main.instance().getLog().info("Registering speed module recipe");
		Sponge.getRegistry().getRecipeRegistry().register(getShapedRecipe(recipes.getNode("speed-module"), Items.getSpeedModule(config.getNode("settings", "speed-module-increment").getInt())));
	}

	public static void remove() {
		try {
			ConfigurationNode config = ConfigManager.get().getConfig();
			ConfigurationNode recipes = config.getNode("recipes");

			Main.instance().getLog().info("Registering spawner recipe");		
			Sponge.getRegistry().getRecipeRegistry().remove(getShapedRecipe(recipes.getNode("spawner"), Items.getSpawner(new Spawner())));
			
			Main.instance().getLog().info("Registering quantity module recipe");
			Sponge.getRegistry().getRecipeRegistry().remove(getShapedRecipe(recipes.getNode("quantity-module"), Items.getQuantityModule(config.getNode("settings", "quantity-module-increment").getInt())));
			
			Main.instance().getLog().info("Registering speed module recipe");
			Sponge.getRegistry().getRecipeRegistry().remove(getShapedRecipe(recipes.getNode("speed-module"), Items.getSpeedModule(config.getNode("settings", "speed-module-increment").getInt())));
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
}
