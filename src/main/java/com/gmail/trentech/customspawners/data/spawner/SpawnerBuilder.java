package com.gmail.trentech.customspawners.data.spawner;

import static com.gmail.trentech.customspawners.data.DataQueries.NAME;
import static com.gmail.trentech.customspawners.data.DataQueries.AMOUNT;
import static com.gmail.trentech.customspawners.data.DataQueries.ENABLE;
import static com.gmail.trentech.customspawners.data.DataQueries.ENTITIES;
import static com.gmail.trentech.customspawners.data.DataQueries.LOCATION;
import static com.gmail.trentech.customspawners.data.DataQueries.RANGE;
import static com.gmail.trentech.customspawners.data.DataQueries.TIME;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.Main;

public class SpawnerBuilder extends AbstractDataBuilder<Spawner> {

	public SpawnerBuilder() {
		super(Spawner.class, 2);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Optional<Spawner> buildContent(DataView container) throws InvalidDataException {
		if (container.contains(NAME, ENTITIES, LOCATION, AMOUNT, TIME, RANGE, ENABLE)) {
			String name = container.getString(NAME).get();
			
			List<EntityType> entities = new ArrayList<>();

			for (String entity : (List<String>) container.getList(ENTITIES).get()) {
				entities.add(Main.getGame().getRegistry().getType(EntityType.class, entity).get());
			}

			String[] args = container.getString(LOCATION).get().split("\\.");

			Optional<World> optionalWorld = Main.getGame().getServer().getWorld(args[0]);

			if (!optionalWorld.isPresent()) {
				return Optional.empty();
			}
			World world = optionalWorld.get();

			Location<World> location = new Location<World>(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));

			int amount = container.getInt(AMOUNT).get();
			int time = container.getInt(TIME).get();
			int range = container.getInt(RANGE).get();
			boolean enable = container.getBoolean(ENABLE).get();

			return Optional.of(new Spawner(name, entities, location, amount, time, range, enable));
		}

		return Optional.empty();
	}
}
