package com.gmail.trentech.mobspawners.data.entity;

import static com.gmail.trentech.mobspawners.data.Keys.ENTITY;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityArchetype;

import com.google.common.base.Preconditions;

public class EntityData extends AbstractSingleData<EntityArchetype, EntityData, ImmutableEntityData> {

	public EntityData(EntityArchetype value) {
		super(value, ENTITY);
	}

	public Value<EntityArchetype> entity() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY, getValue(), getValue());
	}

	@Override
	public EntityData copy() {
		return new EntityData(this.getValue());
	}

	@Override
	public Optional<EntityData> fill(DataHolder dataHolder, MergeFunction mergeFn) {
		EntityData signData = Preconditions.checkNotNull(mergeFn).merge(copy(), dataHolder.get(EntityData.class).orElse(copy()));
		return Optional.of(set(ENTITY, signData.get(ENTITY).get()));
	}

	@Override
	public Optional<EntityData> from(DataContainer container) {
		if (container.contains(ENTITY.getQuery())) {
			return Optional.of(set(ENTITY, container.getSerializable(ENTITY.getQuery(), EntityArchetype.class).orElse(getValue())));
		}
		return Optional.empty();
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public ImmutableEntityData asImmutable() {
		return new ImmutableEntityData(this.getValue());
	}

	@Override
	protected Value<EntityArchetype> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY, getValue(), getValue());
	}

	@Override
	public DataContainer toContainer() {
		return new MemoryDataContainer().set(ENTITY, getValue());
	}

}
