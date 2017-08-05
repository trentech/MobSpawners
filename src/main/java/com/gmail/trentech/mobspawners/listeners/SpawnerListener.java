package com.gmail.trentech.mobspawners.listeners;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.gmail.trentech.mobspawners.data.spawner.SpawnerData;
import com.gmail.trentech.mobspawners.init.Items;
import com.gmail.trentech.pjc.core.ConfigManager;

public class SpawnerListener {

	private static ConcurrentHashMap<UUID, SpawnerData> cache = new ConcurrentHashMap<>();

	@Listener
	public void onClientConnectionEventJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
		if (ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
			for (Spawner spawner : Spawner.get(player)) {
				if (spawner.isEnabled()) {
					Main.instance().spawn(spawner);
				}
			}
		}
		checkItemInHand(player);
	}

	@Listener
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
		if (ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
			for (Spawner spawner : Spawner.get(player)) {
				if (ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "disable-on-logout").getBoolean()) {
					Location<World> location = spawner.getLocation().get();
					String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

					for (Task task : Sponge.getScheduler().getScheduledTasks()) {
						if (task.getName().startsWith("mobspawners:" + name)) {
							task.cancel();
						}
					}
				}
			}
		}
	}

	@Listener(order = Order.POST)
	public void onAffectSlotEvent(AffectSlotEvent event, @Root Player player) {
		Sponge.getScheduler().createTaskBuilder().async().delayTicks(3).execute(task -> {
			checkItemInHand(player);
		}).submit(Main.getPlugin());
	}

	@Listener
	public void onChangeBlockEventPlace(ChangeBlockEvent.Place event, @Root Player player) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getFinal();

			if (!snapshot.getState().getType().equals(BlockTypes.STAINED_GLASS)) {
				continue;
			}

			Optional<Location<World>> optionalLocation = snapshot.getLocation();

			if (!optionalLocation.isPresent()) {
				continue;
			}
			Location<World> location = optionalLocation.get();

			if (cache.containsKey(player.getUniqueId())) {
				Spawner spawner = cache.get(player.getUniqueId()).spawner().get();

				spawner.setLocation(Optional.of(location));
				spawner.setOwner(player);
				spawner.create();

				checkItemInHand(player);
			}
		}
	}

	@Listener
	public void onChangeBlockEventBreak(ChangeBlockEvent.Break event) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getOriginal();

			Optional<Location<World>> optionalLocation = snapshot.getLocation();

			if (!optionalLocation.isPresent()) {
				return;
			}
			Location<World> location = optionalLocation.get();

			Optional<Spawner> optionalSpawner = Spawner.get(location);

			if (!optionalSpawner.isPresent()) {
				return;
			}
			Spawner spawner = optionalSpawner.get();

			spawner.remove();

			Optional<Player> optionalPlayer = event.getCause().first(Player.class);

			if (optionalPlayer.isPresent()) {
				Player player = optionalPlayer.get();

				if (player.gameMode().get().equals(GameModes.CREATIVE)) {
					return;
				}
			}

			ItemStack itemStack = Items.getSpawner(spawner);

			Item item = (Item) location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
			item.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());

			location.getExtent().spawnEntity(item, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(item).type(SpawnTypes.PLUGIN).build())));
		}
	}

	@Listener
	public void onNotifyNeighborBlockEvent(NotifyNeighborBlockEvent event, @First BlockSnapshot snapshot) {
		if (snapshot.get(Keys.POWER).isPresent()) {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<Spawner> optionalSpawner = Spawner.get(location);

				if (optionalSpawner.isPresent()) {
					Spawner spawner = optionalSpawner.get();

					if (spawner.isEnabled() == snapshot.get(Keys.POWER).get() >= 1) {
						spawner.setEnabled(!(snapshot.get(Keys.POWER).get() >= 1));
						spawner.update();
					}
				}
			}
		} else if (snapshot.get(Keys.POWERED).isPresent()) {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<Spawner> optionalSpawner = Spawner.get(location);

				if (optionalSpawner.isPresent()) {
					Spawner spawner = optionalSpawner.get();

					if (spawner.isEnabled() == snapshot.get(Keys.POWERED).get()) {
						spawner.setEnabled(!snapshot.get(Keys.POWERED).get());
						spawner.update();
					}
				}
			}
		} else if (snapshot.getState().getType().equals(BlockTypes.REDSTONE_BLOCK)) {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<Spawner> optionalSpawner = Spawner.get(location);

				if (optionalSpawner.isPresent()) {
					Spawner spawner = optionalSpawner.get();

					if (spawner.isEnabled()) {
						spawner.setEnabled(false);
						spawner.update();
					}
				}
			}
		} else if (snapshot.getState().getType().equals(BlockTypes.REDSTONE_TORCH)) {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<Spawner> optionalSpawner = Spawner.get(location);

				if (optionalSpawner.isPresent()) {
					Spawner spawner = optionalSpawner.get();

					if (spawner.isEnabled()) {
						spawner.setEnabled(false);
						spawner.update();
					}
				}
			}
		} else {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<Spawner> optionalSpawner = Spawner.get(location);

				if (optionalSpawner.isPresent()) {
					Spawner spawner = optionalSpawner.get();

					if (!spawner.isEnabled()) {
						spawner.setEnabled(true);
						spawner.update();
					}
				}
			}
		}
	}

	@Listener
	public void onInteractBlockEventSecondary(InteractBlockEvent.Secondary event, @First Player player) {
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

		if (optionalItemStack.isPresent()) {
			return;
		}
		
		int score = spawner.getEntities().size() + 2;
		
		Scoreboard scoreboard = Scoreboard.builder().build();

		Objective objective = Objective.builder().displayName(Text.of(TextColors.GREEN, "     Spawner Info     ")).name("spawnerinfo").criterion(Criteria.DUMMY).build();

		objective.getOrCreateScore(Text.of(TextColors.GREEN, "Speed: ", TextColors.WHITE, spawner.getTime(), " seconds")).setScore(score--);
		objective.getOrCreateScore(Text.of(TextColors.GREEN, "Quantity: ", TextColors.WHITE, spawner.getAmount())).setScore(score--);
		
		objective.getOrCreateScore(Text.of(TextColors.GREEN, "Entities")).setScore(score--);

		for(EntityArchetype snapshot : spawner.getEntities()) {
			Optional<Text> displayName = snapshot.get(Keys.DISPLAY_NAME);
			
			if(displayName.isPresent()) {	
				objective.getOrCreateScore(Text.of(TextColors.YELLOW, " - ", displayName.get())).setScore(score--);
			} else {
				objective.getOrCreateScore(Text.of(TextColors.YELLOW, " - ", snapshot.getType().getTranslation())).setScore(score--);
			}		
		}

		scoreboard.addObjective(objective);
		scoreboard.updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

		player.setScoreboard(scoreboard);

		Sponge.getScheduler().createTaskBuilder().async().delayTicks(100).execute(runnable -> {
			player.setScoreboard(Scoreboard.builder().build());
		}).submit(Main.getPlugin());
	}
	
	public static void checkItemInHand(Player player) {
		Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);

		if (optionalItemStack.isPresent()) {
			ItemStack itemStack = optionalItemStack.get();

			Optional<Text> optionalName = itemStack.get(Keys.DISPLAY_NAME);
			
			if(!optionalName.isPresent()) {
				return;
			}		

			if(!optionalName.get().toPlain().equalsIgnoreCase("Spawner")) {
				return;
			}
			Optional<SpawnerData> optionalData = itemStack.get(SpawnerData.class);

			SpawnerData data;
			if (!optionalData.isPresent()) {
				data = new SpawnerData(new Spawner());
				
				itemStack.offer(data);
				
				player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
			} else {
				data = optionalData.get();
			}

			cache.put(player.getUniqueId(), data);

			return;		
		}

		cache.remove(player.getUniqueId());
	}
}
