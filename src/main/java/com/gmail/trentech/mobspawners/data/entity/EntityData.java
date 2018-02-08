package com.gmail.trentech.mobspawners.data.entity;

import static com.gmail.trentech.mobspawners.data.DataKeys.ENTITY_DATA;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityArchetype;

import com.gmail.trentech.mobspawners.data.entity.EntityData.Immutable;
import com.google.common.base.Preconditions;

public class EntityData extends AbstractSingleData<EntityArchetype, EntityData, Immutable> {

	public EntityData(EntityArchetype value) {
		super(value, ENTITY_DATA);
	}

	public Value<EntityArchetype> entity() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY_DATA, getValue());
	}

	@Override
	protected Value<EntityArchetype> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY_DATA, getValue());
	}
	
	@Override
	public EntityData copy() {
		return new EntityData(this.getValue());
	}

	@Override
	public Optional<EntityData> fill(DataHolder dataHolder, MergeFunction mergeFunction) {
        EntityData signData = Preconditions.checkNotNull(mergeFunction).merge(copy(), dataHolder.get(EntityData.class).orElse(copy()));
		return Optional.of(set(ENTITY_DATA, signData.get(ENTITY_DATA).get()));
	}

	@Override
	public Optional<EntityData> from(DataContainer container) {
		if (container.contains(ENTITY_DATA.getQuery())) {
			return Optional.of(set(ENTITY_DATA, container.getSerializable(ENTITY_DATA.getQuery(), EntityArchetype.class).orElse(getValue())));
		}
		return Optional.empty();
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
		return super.toContainer().set(ENTITY_DATA, getValue());
	}

	public static class Immutable extends AbstractImmutableSingleData<EntityArchetype, Immutable, EntityData> {

		protected Immutable(EntityArchetype value) {
			super(value, ENTITY_DATA);
		}

		public ImmutableValue<EntityArchetype> entity() {
			return Sponge.getRegistry().getValueFactory().createValue(ENTITY_DATA, getValue()).asImmutable();
		}

		@Override
		public <E> Optional<Immutable> with(Key<? extends BaseValue<E>> key, E value) {
			if (this.supports(key)) {
				return Optional.of(asMutable().set(key, value).asImmutable());
			} else {
				return Optional.empty();
			}
		}
		
		@Override
		protected ImmutableValue<EntityArchetype> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(ENTITY_DATA, getValue()).asImmutable();
		}

		@Override
		public EntityData asMutable() {
			return new EntityData(this.getValue());
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
		public Optional<EntityData> buildContent(DataView container) throws InvalidDataException {
			if (!container.contains(ENTITY_DATA.getQuery())) {
				return Optional.empty();
			}
			EntityArchetype entity = EntityArchetype.builder().build(container.getView(ENTITY_DATA.getQuery()).get()).get();

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
