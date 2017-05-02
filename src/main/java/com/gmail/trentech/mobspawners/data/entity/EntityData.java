package com.gmail.trentech.mobspawners.data.entity;

import static com.gmail.trentech.mobspawners.data.DataKeys.ENTITY_ARCHE;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityArchetype;

import com.gmail.trentech.mobspawners.data.entity.EntityData.Immutable;

public class EntityData extends AbstractSingleData<EntityArchetype, EntityData, Immutable> {

	public EntityData(EntityArchetype value) {
		super(value, ENTITY_ARCHE);
	}

	public Value<EntityArchetype> entity() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY_ARCHE, getValue());
	}

	@Override
	protected Value<EntityArchetype> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY_ARCHE, getValue());
	}
	
	@Override
	public EntityData copy() {
		return new EntityData(getValue());
	}

	@Override
	public Optional<EntityData> fill(DataHolder dataHolder, MergeFunction mergeFunction) {
		Optional<EntityData> optionalData = dataHolder.get(EntityData.class);
		
        if (optionalData.isPresent()) {
        	EntityData data = optionalData.get();
        	EntityData finalData = mergeFunction.merge(this, data);
            setValue(finalData.getValue());
        }
        
        return Optional.of(this);
	}

	@Override
	public Optional<EntityData> from(DataContainer container) {
		return Optional.of(this);
	}

	@Override
	public Immutable asImmutable() {
		return new EntityData.Immutable(getValue());
	}

	@Override
	public int getContentVersion() {
		return 1;
	}
	
	@Override
	public DataContainer toContainer() {
		return DataContainer.createNew().set(ENTITY_ARCHE, getValue());
	}

	public static class Immutable extends AbstractImmutableSingleData<EntityArchetype, Immutable, EntityData> {

		protected Immutable(EntityArchetype value) {
			super(value, ENTITY_ARCHE);
		}

		public ImmutableValue<EntityArchetype> entity() {
			return Sponge.getRegistry().getValueFactory().createValue(ENTITY_ARCHE, getValue()).asImmutable();
		}

		@Override
		protected ImmutableValue<EntityArchetype> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(ENTITY_ARCHE, getValue()).asImmutable();
		}

		@Override
		public EntityData asMutable() {
			return new EntityData(getValue());
		}
		
		@Override
		public int getContentVersion() {
			return 1;
		}
	}
	
	public static class Builder extends AbstractDataBuilder<EntityData> implements DataManipulatorBuilder<EntityData, Immutable> {

		public Builder() {
			super(EntityData.class, 1);
		}

		@Override
		protected Optional<EntityData> buildContent(DataView container) throws InvalidDataException {
			if (!container.contains(ENTITY_ARCHE.getQuery())) {
				return Optional.empty();
			}

			EntityArchetype entity = container.getSerializable(ENTITY_ARCHE.getQuery(), EntityArchetype.class).get();

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
	}
}
