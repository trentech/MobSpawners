package com.gmail.trentech.mobspawners.data.spawner;

import static com.gmail.trentech.mobspawners.data.Keys.SPAWNER;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class SpawnerDataManipulatorBuilder extends AbstractDataBuilder<SpawnerData> implements DataManipulatorBuilder<SpawnerData, ImmutableSpawnerData> {

	public SpawnerDataManipulatorBuilder() {
		super(SpawnerData.class, 1);
	}

	@Override
	protected Optional<SpawnerData> buildContent(DataView container) throws InvalidDataException {
		if (!container.contains(SPAWNER.getQuery())) {
			return Optional.empty();
		}
		Spawner spawner = container.getSerializable(SPAWNER.getQuery(), Spawner.class).get();
		
		return Optional.of(new SpawnerData(spawner));
	}

	@Override
	public SpawnerData create() {
		return new SpawnerData();
	}

	@Override
	public Optional<SpawnerData> createFrom(DataHolder dataHolder) {
		return create().fill(dataHolder);
	}

	public SpawnerData createFrom(Spawner spawner) {
		return new SpawnerData(spawner);
	}

}
