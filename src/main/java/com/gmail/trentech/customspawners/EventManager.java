package com.gmail.trentech.customspawners;

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.data.spawner.Spawner;

public class EventManager {

	@Listener
	public void onChangeBlockEventBreak(ChangeBlockEvent.Break event) {
		for(Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Optional<Location<World>> optionalLocation = transaction.getOriginal().getLocation();
			
			if(!optionalLocation.isPresent()) {
				continue;
			}
			Location<World> location = optionalLocation.get();
			
			Optional<Spawner> optionalSpawner = Spawner.get(location);
			
			if(!optionalSpawner.isPresent()) {
				continue;
			}
			Spawner spawner = optionalSpawner.get();
			spawner.remove();
		}
	}
}
