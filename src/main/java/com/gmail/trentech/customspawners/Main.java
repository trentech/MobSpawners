package com.gmail.trentech.customspawners;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.commands.CommandManager;
import com.gmail.trentech.customspawners.data.spawner.Spawner;
import com.gmail.trentech.customspawners.data.spawner.SpawnerBuilder;
import com.gmail.trentech.customspawners.utils.ConfigManager;
import com.gmail.trentech.customspawners.utils.Resource;
import com.gmail.trentech.customspawners.utils.SQLUtils;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;
import net.minecrell.mcstats.SpongeStatsLite;

@Updatifier(repoName = "CustomSpawners", repoOwner = "TrenTech", version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, authors = Resource.AUTHOR, url = Resource.URL, description = Resource.DESCRIPTION, dependencies = {@Dependency(id = "Updatifier", optional = true)})
public class Main {

    @Inject
    private SpongeStatsLite stats;
    
	private static Game game;
	private static Logger log;	
	private static PluginContainer plugin;

	private static ThreadLocalRandom random = ThreadLocalRandom.current();
	private static ParticleEffect particle = ParticleEffect.builder().type(ParticleTypes.FLAME).build();

	@Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
		
		if(this.stats.start()) {
			getLog().info("MCStats started.");
		}else{
			getLog().warn("Could not start MCStats. This could be due to server opt-out, or error.");
		}
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
    	ConfigManager configManager = new ConfigManager();
    	configManager.init();

    	getGame().getDataManager().registerBuilder(Spawner.class, new SpawnerBuilder());
    	getGame().getCommandManager().register(this, new CommandManager().cmdSpawner, "spawner", "cs");

    	SQLUtils.createTables();
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
    	Spawner.init();
    }

	public static Logger getLog() {
        return log;
    }
    
	public static Game getGame() {
		return game;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}
	
	public static void spawn(String name, Spawner spawner){
		Optional<Location<World>> optionalLocation = spawner.getLocation();
		
		if(!optionalLocation.isPresent()) {
			getLog().error("Cannot create spawner scheduler. Location error");
			return;
		}
		Location<World> startLocation = optionalLocation.get();
		
		EntityType entityType = spawner.getEntity();

        Main.getGame().getScheduler().createTaskBuilder().interval(spawner.getTime(), TimeUnit.SECONDS).name(name).execute(t -> {
        	for(int i = 0; i < spawner.getAmount(); i++) {
        		Location<World> location = startLocation;
        		
        		if(spawner.getRadius() > 1) {
        			location = getRandomLocation(startLocation, spawner.getRadius());
        		}
        		
        		Optional<Entity> optionalEntity = location.getExtent().createEntity(entityType, location.getPosition());
        		
        		if(optionalEntity.isPresent()) {
        			Entity entity = optionalEntity.get();
        			
        			location.getExtent().spawnEntity(entity, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build())));

        			for(int x = 0; x < 9; x++) {
        				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble() - .5, random.nextDouble() - .5, random.nextDouble() - .5));
        				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble() - .5, random.nextDouble() - .5, random.nextDouble() - .5));
        			}
        		}
        	}
        }).submit(Main.getPlugin());
	}
	
	private static Location<World> getRandomLocation(Location<World> location, int radius) {
		TeleportHelper teleportHelper = Main.getGame().getTeleportHelper();

		for(int i = 0; i < 19; i++) {
			int x = (random.nextInt(radius*2) - radius) + location.getBlockX();
			int z = (random.nextInt(radius*2) - radius) + location.getBlockZ();

			Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(location.getExtent().getLocation(x, location.getBlockY(), z));

			if(!optionalLocation.isPresent()) {
				continue;
			}			
			return optionalLocation.get();
		}
		return location;
	}
}