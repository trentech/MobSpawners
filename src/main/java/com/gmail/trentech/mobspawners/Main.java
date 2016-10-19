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
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
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
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.gmail.trentech.helpme.Help;
import com.gmail.trentech.mobspawners.commands.CommandManager;
import com.gmail.trentech.mobspawners.data.spawner.ImmutableSpawnerData;
import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.gmail.trentech.mobspawners.data.spawner.SpawnerBuilder;
import com.gmail.trentech.mobspawners.data.spawner.SpawnerData;
import com.gmail.trentech.mobspawners.data.spawner.SpawnerDataManipulatorBuilder;
import com.gmail.trentech.mobspawners.init.Recipes;
import com.gmail.trentech.mobspawners.listeners.EntityModuleListener;
import com.gmail.trentech.mobspawners.listeners.QuantityModuleListener;
import com.gmail.trentech.mobspawners.listeners.SpawnerListener;
import com.gmail.trentech.mobspawners.listeners.SpeedModuleListener;
import com.gmail.trentech.mobspawners.utils.ConfigManager;
import com.gmail.trentech.mobspawners.utils.Resource;
import com.gmail.trentech.mobspawners.utils.SQLUtils;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path path;

	@Inject
	private Logger log;
	private ThreadLocalRandom random = ThreadLocalRandom.current();
	private ParticleEffect particle = ParticleEffect.builder().type(ParticleTypes.FLAME).build();

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
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		ConfigManager.init();
		ConfigManager.init("recipes");

		Sponge.getEventManager().registerListeners(this, new SpawnerListener());
		Sponge.getEventManager().registerListeners(this, new EntityModuleListener());
		Sponge.getEventManager().registerListeners(this, new SpeedModuleListener());
		Sponge.getEventManager().registerListeners(this, new QuantityModuleListener());

		Sponge.getDataManager().registerBuilder(Spawner.class, new SpawnerBuilder());
		Sponge.getDataManager().register(SpawnerData.class, ImmutableSpawnerData.class, new SpawnerDataManipulatorBuilder());
		Sponge.getCommandManager().register(this, new CommandManager().cmdSpawner, "spawner", "ms");

		SQLUtils.createTables();

		try {
			Recipes.register();
		} catch (Exception e) {
			getLog().warn("Recipe registration failed. This could be an implementation error.");
		}
		
		if(Sponge.getPluginManager().isLoaded("helpme")) {
			Help spawnerCreate = new Help("spawner create", "create", "Temporary command to create mob spawner")
					.setPermission("mobspawners.cmd.spawner.create")
					.addUsage("/spawner create <entity>")
					.addUsage("/ms c <entity>")
					.addExample("/spawner create minecraft:zombie");
			
			Help spawnerList = new Help("spawner list", "list", "List all spawners and their locations")
					.setPermission("mobspawners.cmd.spawner.list")
					.addUsage("/spawner list")
					.addUsage("/ms ls")
					.addExample("/spawner list");
			
			Help spawnerModuleEntity = new Help("spawner module entity", "entity", "Temporary command to create mob module")
					.setPermission("mobspawners.cmd.spawner.module.entity")
					.addUsage("/spawner module entity <entity>")
					.addUsage("/ms m e <entity>")
					.addExample("/spawner module entity minecraft:creeper");
			
			Help spawnerModuleQuantity = new Help("spawner module quantity", "quantity", "Temporary command to create quantity module")
					.setPermission("mobspawners.cmd.spawner.module.quantity")
					.addUsage("/spawner module quantity")
					.addUsage("/ms m q")
					.addExample("/spawner module quantity");
			
			Help spawnerModuleSpeed = new Help("spawner module speed", "speed", "Temporary command to create speed module")
					.setPermission("mobspawners.cmd.spawner.module.speed")
					.addUsage("/spawner module speed")
					.addUsage("/ms m s")
					.addExample("/spawner module speed");
			
			Help spawnerModule = new Help("spawner module", "module", "Subcommand for modules")
					.setPermission("mobspawners.cmd.spawner.module")
					.addUsage("/spawner module")
					.addUsage("/ms m")
					.addExample("/spawner module")
					.addChild(spawnerModuleSpeed)
					.addChild(spawnerModuleQuantity)
					.addChild(spawnerModuleEntity);
			
			Help spawner = new Help("spawner", "spawner", "Base command for MobSpawners")
					.setPermission("mobspawners.cmd.spawner.module")
					.addChild(spawnerList)
					.addChild(spawnerCreate)
					.addChild(spawnerModule);
			
			Help.register(spawner);
		}
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
		
		try {
			Recipes.remove();
		} catch (Exception e) {
			getLog().warn("Recipe registration failed. This could be an implementation error.");
		}
		
		ConfigManager.init();
		ConfigManager.init("recipes");
		
		try {
			Recipes.register();
		} catch (Exception e) {
			getLog().warn("Recipe registration failed. This could be an implementation error.");
		}
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
			List<EntityType> entities = spawner.getEntities();

			Sponge.getScheduler().createTaskBuilder().delay(spawner.getTime() / 2, TimeUnit.SECONDS).interval(spawner.getTime(), TimeUnit.SECONDS).name("mobspawners:" + name + ":spawn").execute(t -> {
				if (world.isLoaded()) {
					Optional<Chunk> optionalChunk = world.getChunk(spawnerLocation.getChunkPosition());

					if (optionalChunk.isPresent() && optionalChunk.get().isLoaded()) {
						int amount = random.nextInt(spawner.getAmount()) + 1;

						for (int i = 0; i < amount; i++) {

							location.set(getRandomLocation(spawnerLocation, spawner.getRadius()));

							EntityType entityType = entities.get(random.nextInt(entities.size()));

							Entity entity = location.get().getExtent().createEntity(entityType, location.get().getPosition());

							location.get().getExtent().spawnEntity(entity, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build())));

							for (int x = 0; x < 9; x++) {
								location.get().getExtent().spawnParticles(particle, location.get().getPosition().add(random.nextDouble() - .5, random.nextDouble() - .5, random.nextDouble() - .5));
								location.get().getExtent().spawnParticles(particle, location.get().getPosition().add(random.nextDouble() - .5, random.nextDouble() - .5, random.nextDouble() - .5));

								spawnerLocation.getExtent().spawnParticles(spawnParticle, spawnerLocation.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
								spawnerLocation.getExtent().spawnParticles(spawnParticle, spawnerLocation.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
							}
						}
					}

				}
			}).submit(getPlugin());

			Sponge.getScheduler().createTaskBuilder().interval(70, TimeUnit.MILLISECONDS).name("mobspawners:" + name + ":particle").execute(t -> {
				ParticleEffect particle = ParticleEffect.builder().type(ParticleTypes.FLAME).build();

				if (world.isLoaded()) {
					Optional<Chunk> optionalChunk = world.getChunk(spawnerLocation.getChunkPosition());

					if (optionalChunk.isPresent() && optionalChunk.get().isLoaded()) {
						world.spawnParticles(particle, spawnerLocation.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
					}
				}
			}).submit(getPlugin());
		}
	}

	private Location<World> getRandomLocation(Location<World> location, int radius) {
		TeleportHelper teleportHelper = Sponge.getGame().getTeleportHelper();

		for (int i = 0; i < 19; i++) {
			double x = random.nextDouble() * (radius * 2) - radius;
            double z = random.nextDouble() * (radius * 2) - radius;

			Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(location.add(x, 0, z));

			if (!optionalLocation.isPresent()) {
				continue;
			}

			if (optionalLocation.equals(location)) {
				continue;
			}

			return optionalLocation.get();
		}
		return location;
	}
}