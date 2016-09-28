package com.gmail.trentech.mobspawners.data.spawner;

import static com.gmail.trentech.mobspawners.data.DataQueries.AMOUNT;
import static com.gmail.trentech.mobspawners.data.DataQueries.ENABLE;
import static com.gmail.trentech.mobspawners.data.DataQueries.ENTITIES;
import static com.gmail.trentech.mobspawners.data.DataQueries.LOCATION;
import static com.gmail.trentech.mobspawners.data.DataQueries.OWNER;
import static com.gmail.trentech.mobspawners.data.DataQueries.RANGE;
import static com.gmail.trentech.mobspawners.data.DataQueries.TIME;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SpawnerBuilder extends AbstractDataBuilder<Spawner> {

	public SpawnerBuilder() {
		super(Spawner.class, 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Optional<Spawner> buildContent(DataView container) throws InvalidDataException {
		if (container.contains(ENTITIES, AMOUNT, TIME, RANGE, ENABLE, OWNER)) {
			List<EntityType> entities = new ArrayList<>();

			for (String entity : (List<String>) container.getList(ENTITIES).get()) {
				entities.add(Sponge.getRegistry().getType(EntityType.class, entity).get());
			}

			int amount = container.getInt(AMOUNT).get();
			int time = container.getInt(TIME).get();
			int range = container.getInt(RANGE).get();
			boolean enable = container.getBoolean(ENABLE).get();

			UUID owner = UUID.fromString(container.getString(OWNER).get());

			if (container.contains(LOCATION)) {
				String[] args = container.getString(LOCATION).get().split("\\.");

				Optional<World> optionalWorld = Sponge.getServer().getWorld(args[0]);

				if (!optionalWorld.isPresent()) {
					return Optional.empty();
				}
				World world = optionalWorld.get();

				Location<World> location = new Location<World>(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));

				return Optional.of(new Spawner(entities, location, amount, time, range, enable, owner));
			} else {
				return Optional.of(new Spawner(entities, amount, time, range, enable, owner));
			}

		}

		return Optional.empty();
	}
}
