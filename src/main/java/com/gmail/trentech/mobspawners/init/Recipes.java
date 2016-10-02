package com.gmail.trentech.mobspawners.init;

import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.EntityType;
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

	public static void register() {
		try {			
			ConfigurationNode config = ConfigManager.get().getConfig();
			ConfigurationNode mob = config.getNode("mob");
	
			for (Entry<Object, ? extends ConfigurationNode> child : mob.getChildrenMap().entrySet()) {
				ConfigurationNode childNode = child.getValue();
	
				String key = childNode.getKey().toString();
	
				Optional<EntityType> optionalEntityType = Sponge.getRegistry().getType(EntityType.class, key);
	
				if (optionalEntityType.isPresent()) {
					EntityType entityType = optionalEntityType.get();
	
	
					Main.instance().getLog().info("Registering spawner and module recipes for " + entityType.getId());
					Sponge.getRegistry().getRecipeRegistry().register(getEntityModuleRecipe(entityType, childNode.getString()));
					Sponge.getRegistry().getRecipeRegistry().register(getSpawnerRecipe(entityType, childNode.getString()));
				}
			}
			
			Main.instance().getLog().info("Registering quantity module recipe");			
			Sponge.getRegistry().getRecipeRegistry().register(getQuantityModuleRecipe());
			Main.instance().getLog().info("Registering speed module recipe");			
			Sponge.getRegistry().getRecipeRegistry().register(getSpeedModuleRecipe());
		} catch (InvalidItemTypeException e) {
			e.printStackTrace();
		}
		
	}

	private static ShapedRecipe getQuantityModuleRecipe() throws InvalidItemTypeException {
		ConfigurationNode config = ConfigManager.get().getConfig();
		ConfigurationNode recipe = config.getNode("recipe", "quantity_module");
		
		Builder builder = Sponge.getRegistry().createBuilder(ShapedRecipe.Builder.class);

		for(Entry<Object, ? extends ConfigurationNode> child : recipe.getChildrenMap().entrySet()) {
			ConfigurationNode childNode = child.getValue();
			
			String key = childNode.getKey().toString();
			String itemId = childNode.getString();
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
				throw new InvalidItemTypeException("ItemType in config.conf at " + childNode.getKey().toString() + " is invalid");
			}
		}

		return builder.addResult(Items.getSpeedModule(config.getNode("settings", "speed_module_increment").getInt())).build();		
	}
	
	private static ShapedRecipe getSpeedModuleRecipe() throws InvalidItemTypeException {
		ConfigurationNode config = ConfigManager.get().getConfig();
		ConfigurationNode recipe = config.getNode("recipe", "speed_module");
		
		Builder builder = Sponge.getRegistry().createBuilder(ShapedRecipe.Builder.class);

		for(Entry<Object, ? extends ConfigurationNode> child : recipe.getChildrenMap().entrySet()) {
			ConfigurationNode childNode = child.getValue();
			
			String key = childNode.getKey().toString();
			String itemId = childNode.getString();
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
				throw new InvalidItemTypeException("ItemType in config.conf at " + childNode.getKey().toString() + " is invalid");
			}
		}

		return builder.addResult(Items.getSpeedModule(config.getNode("settings", "speed_module_increment").getInt())).build();		
	}
	
	private static ShapedRecipe getEntityModuleRecipe(EntityType entity, String item) throws InvalidItemTypeException {
		ConfigurationNode config = ConfigManager.get().getConfig();
		ConfigurationNode recipe = config.getNode("recipe", "entity_module");

		Builder builder = Sponge.getRegistry().createBuilder(ShapedRecipe.Builder.class);

		for (Entry<Object, ? extends ConfigurationNode> child : recipe.getChildrenMap().entrySet()) {
			ConfigurationNode childNode = child.getValue();

			String key = childNode.getKey().toString();
			String value = childNode.getString();

			String[] args;
			if (value.equals("%ENTITY%")) {
				args = item.split(":");
			} else {
				args = value.split(":");
			}

			String itemId = args[0] + ":" + args[1];

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
				throw new InvalidItemTypeException("ItemType in config.conf at " + childNode.getKey().toString() + " is invalid");
			}
		}

		return builder.addResult(Items.getEntityModule(entity)).build();
	}
	
	private static ShapedRecipe getSpawnerRecipe(EntityType entity, String item) throws InvalidItemTypeException {
		ConfigurationNode config = ConfigManager.get().getConfig();
		ConfigurationNode recipe = config.getNode("recipe", "spawner");

		Builder builder = Sponge.getRegistry().createBuilder(ShapedRecipe.Builder.class);

		for (Entry<Object, ? extends ConfigurationNode> child : recipe.getChildrenMap().entrySet()) {
			ConfigurationNode childNode = child.getValue();

			String key = childNode.getKey().toString();
			String value = childNode.getString();

			String[] args;
			if (value.equals("%ENTITY%")) {
				args = item.split(":");
			} else {
				args = value.split(":");
			}

			String itemId = args[0] + ":" + args[1];

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
				throw new InvalidItemTypeException("ItemType in config.conf at " + childNode.getKey().toString() + " is invalid");
			}
		}

		return builder.addResult(Items.getSpawner(new Spawner(entity))).build();
	}
}
