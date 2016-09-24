package com.gmail.trentech.customspawners;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.data.spawner.Spawner;

public class EventListener {

	@Listener
	public void onBlockChangeEvent(ChangeBlockEvent.Break event) {
		for(Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getOriginal();
			
			Optional<Location<World>> optionalLocation = snapshot.getLocation();
			
			if(!optionalLocation.isPresent()) {
				return;
			}
			Location<World> location = optionalLocation.get();
			
			Optional<Spawner> optionalSpawner = Spawner.get(location);
			
			if(!optionalSpawner.isPresent()) {
				return;
			}
			Spawner spawner = optionalSpawner.get();
			
			spawner.remove();

			Optional<Player> optionalPlayer = event.getCause().first(Player.class);
			
			if(!optionalPlayer.isPresent()) {
				return;
			}

			int amount = ThreadLocalRandom.current().nextInt(6) + 1;

			ExperienceOrb entity = (ExperienceOrb) location.getExtent().createEntity(EntityTypes.EXPERIENCE_ORB, location.getPosition());
			
			entity.experienceHeld().experience().set(amount);

			location.getExtent().spawnEntity(entity, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build())));
		}
	}
}
