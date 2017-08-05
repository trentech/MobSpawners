package com.gmail.trentech.mobspawners;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.mobspawners.commands.CommandManager;
import com.gmail.trentech.mobspawners.data.LocationSerializable;
import com.gmail.trentech.mobspawners.data.entity.EntityData;
import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.gmail.trentech.mobspawners.data.spawner.SpawnerData;
import com.gmail.trentech.mobspawners.init.Common;
import com.gmail.trentech.mobspawners.listeners.EntityModuleListener;
import com.gmail.trentech.mobspawners.listeners.QuantityModuleListener;
import com.gmail.trentech.mobspawners.listeners.SpawnerListener;
import com.gmail.trentech.mobspawners.listeners.SpeedModuleListener;
import com.gmail.trentech.mobspawners.utils.Resource;
import com.gmail.trentech.pjc.core.TeleportManager;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true), @Dependency(id = "pjc", optional = false) })
public class Main {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path path;

	@Inject
	private Logger log;
	private ThreadLocalRandom random = ThreadLocalRandom.current();

	private static PluginContainer plugin;
	private static Main instance;

	@Listener
	public void onPreInitializationEvent(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		instance = this;

		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Common.initConfig();		
		Common.initRecipes();
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		Sponge.getDataManager().registerBuilder(LocationSerializable.class, new LocationSerializable.Builder());

		DataRegistration.builder().dataClass(EntityData.class).immutableClass(EntityData.Immutable.class)
			.builder(new EntityData.Builder()).dataName("Entity").manipulatorId("mobspawners_entity").buildAndRegister(Main.getPlugin());
		
		Sponge.getDataManager().registerBuilder(Spawner.class, new Spawner.Builder());
		
		DataRegistration.builder().dataClass(SpawnerData.class).immutableClass(SpawnerData.Immutable.class)
			.builder(new SpawnerData.Builder()).dataName("Spawner").manipulatorId("mobspawners_spawner").buildAndRegister(Main.getPlugin());

		Sponge.getEventManager().registerListeners(this, new SpawnerListener());
		Sponge.getEventManager().registerListeners(this, new EntityModuleListener());
		Sponge.getEventManager().registerListeners(this, new SpeedModuleListener());
		Sponge.getEventManager().registerListeners(this, new QuantityModuleListener());
		
		Common.initData();

		Sponge.getCommandManager().register(this, new CommandManager().cmdSpawner, "spawner", "s");
		
		Common.initHelp();
	}

	@Listener
	public void onStartedServer(GameStartedServerEvent event) {
		Spawner.init();
	}

	@Listener
	public void onReloadEvent(GameReloadEvent event) {
		Sponge.getEventManager().unregisterPluginListeners(getPlugin());
		
		Sponge.getEventManager().registerListeners(this, new SpawnerListener());
		Sponge.getEventManager().registerListeners(this, new EntityModuleListener());
		Sponge.getEventManager().registerListeners(this, new SpeedModuleListener());
		Sponge.getEventManager().registerListeners(this, new QuantityModuleListener());
		
		Common.initConfig();
		
		// Common.initRecipes();
	}
	
	public Logger getLog() {
		return log;
	}

	public Path getPath() {
		return path;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}

	public static Main instance() {
		return instance;
	}

	public void spawn(Spawner spawner) {
		Optional<Location<World>> optionalLocation = spawner.getLocation();

		if (optionalLocation.isPresent()) {
			AtomicReference<Location<World>> location = new AtomicReference<>(spawner.getLocation().get());

			Location<World> spawnerLocation = spawner.getLocation().get();
			String name = spawnerLocation.getExtent().getName() + "." + spawnerLocation.getBlockX() + "." + spawnerLocation.getBlockY() + "." + spawnerLocation.getBlockZ();

			if (spawnerLocation.getRelative(Direction.UP).getBlock().getType().equals(BlockTypes.REDSTONE_BLOCK) || spawnerLocation.getRelative(Direction.DOWN).getBlock().getType().equals(BlockTypes.REDSTONE_BLOCK) || spawnerLocation.getRelative(Direction.NORTH).getBlock().getType().equals(BlockTypes.REDSTONE_BLOCK) || spawnerLocation.getRelative(Direction.SOUTH).getBlock().getType().equals(BlockTypes.REDSTONE_BLOCK) || spawnerLocation.getRelative(Direction.EAST).getBlock().getType().equals(BlockTypes.REDSTONE_BLOCK) || spawnerLocation.getRelative(Direction.WEST).getBlock().getType().equals(BlockTypes.REDSTONE_BLOCK)) {
				return;
			}

			if (spawnerLocation.getRelative(Direction.UP).getBlock().getType().equals(BlockTypes.REDSTONE_TORCH) || spawnerLocation.getRelative(Direction.DOWN).getBlock().getType().equals(BlockTypes.REDSTONE_TORCH) || spawnerLocation.getRelative(Direction.NORTH).getBlock().getType().equals(BlockTypes.REDSTONE_TORCH) || spawnerLocation.getRelative(Direction.SOUTH).getBlock().getType().equals(BlockTypes.REDSTONE_TORCH) || spawnerLocation.getRelative(Direction.EAST).getBlock().getType().equals(BlockTypes.REDSTONE_TORCH) || spawnerLocation.getRelative(Direction.WEST).getBlock().getType().equals(BlockTypes.REDSTONE_TORCH)) {
				return;
			}

			World world = spawnerLocation.getExtent();
			ParticleEffect spawnParticle = ParticleEffect.builder().type(ParticleTypes.FLAME).quantity(3).build();
			List<EntityArchetype> entities = spawner.getEntities();

			Sponge.getScheduler().createTaskBuilder().delay(spawner.getTime() / 2, TimeUnit.SECONDS).interval(spawner.getTime(), TimeUnit.SECONDS).name("mobspawners:" + name + ":spawn").execute(t -> {
				if (world.isLoaded() && entities.size() != 0) {
					Optional<Chunk> optionalChunk = world.getChunk(spawnerLocation.getChunkPosition());

					if (optionalChunk.isPresent() && optionalChunk.get().isLoaded()) {
						int amount = random.nextInt(spawner.getAmount()) + 1;

						for (int i = 0; i < amount; i++) {
							location.set(getRandomLocation(spawnerLocation, spawner.getRadius()));
							
							EntityArchetype snapshot = entities.get(random.nextInt(entities.size()));

							snapshot.apply(location.get(), Cause.of(NamedCause.source(this)));

							for (int x = 0; x < 9; x++) {
								location.get().getExtent().spawnParticles(spawnParticle, location.get().getPosition().add(random.nextDouble() - .5, random.nextDouble() - .5, random.nextDouble() - .5));
								location.get().getExtent().spawnParticles(spawnParticle, location.get().getPosition().add(random.nextDouble() - .5, random.nextDouble() - .5, random.nextDouble() - .5));

								spawnerLocation.getExtent().spawnParticles(spawnParticle, spawnerLocation.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
								spawnerLocation.getExtent().spawnParticles(spawnParticle, spawnerLocation.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
							}
						}
					}

				}
			}).submit(getPlugin());

			Sponge.getScheduler().createTaskBuilder().interval(70, TimeUnit.MILLISECONDS).name("mobspawners:" + name + ":particle").execute(t -> {
				if (world.isLoaded()) {
					Optional<Chunk> optionalChunk = world.getChunk(spawnerLocation.getChunkPosition());

					if (optionalChunk.isPresent() && optionalChunk.get().isLoaded()) {
						ParticleEffect particle;
						
						if(entities.size() == 0) {
							particle = ParticleEffect.builder().type(ParticleTypes.SMOKE).build();
						} else {
							particle = ParticleEffect.builder().type(ParticleTypes.FLAME).build();
						}
						
						world.spawnParticles(particle, spawnerLocation.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
					}
				}
			}).submit(getPlugin());
		}
	}

	public Location<World> getRandomLocation(Location<World> location, int radius) {
		for (int i = 0; i < 19; i++) {
			double x = random.nextDouble() * (radius * 2) - radius;
            double z = random.nextDouble() * (radius * 2) - radius;
            
			Optional<Location<World>> optionalLocation = TeleportManager.getSafeLocation(location.add(x, 0, z));

			if (!optionalLocation.isPresent()) {
				continue;
			}

			if (optionalLocation.get().equals(location)) {
				continue;
			}

			return optionalLocation.get();
		}
		return location;
	}
}