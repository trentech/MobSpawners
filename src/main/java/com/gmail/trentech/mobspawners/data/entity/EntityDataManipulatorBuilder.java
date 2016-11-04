package com.gmail.trentech.mobspawners.data.entity;

import static com.gmail.trentech.mobspawners.data.Keys.ENTITY;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.EntityArchetype;

public class EntityDataManipulatorBuilder extends AbstractDataBuilder<EntityData> implements DataManipulatorBuilder<EntityData, ImmutableEntityData> {

	public EntityDataManipulatorBuilder() {
		super(EntityData.class, 1);
	}

	@Override
	protected Optional<EntityData> buildContent(DataView container) throws InvalidDataException {
		if (!container.contains(ENTITY.getQuery())) {
			return Optional.empty();
		}

		EntityArchetype entity = container.getSerializable(ENTITY.getQuery(), EntityArchetype.class).get();

		return Optional.of(new EntityData(entity));
	}

	@Override
	public EntityData create() {
		return new EntityData(EntityArchetype.builder().build());
	}

	@Override
	public Optional<EntityData> createFrom(DataHolder dataHolder) {
		return create().fill(dataHolder);
	}

	public EntityData createFrom(EntityArchetype entity) {
		return new EntityData(entity);
	}

}
