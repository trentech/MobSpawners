package com.gmail.trentech.mobspawners.data.entity;

import static com.gmail.trentech.mobspawners.data.Keys.ENTITY;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.EntityArchetype;

public class ImmutableEntityData extends AbstractImmutableSingleData<EntityArchetype, ImmutableEntityData, EntityData> {

	protected ImmutableEntityData(EntityArchetype value) {
		super(value, ENTITY);
	}

	public ImmutableValue<EntityArchetype> entity() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY, getValue(), getValue()).asImmutable();
	}

	@Override
	public <E> Optional<ImmutableEntityData> with(Key<? extends BaseValue<E>> key, E value) {
		if (this.supports(key)) {
			return Optional.of(asMutable().set(key, value).asImmutable());
		} else {
			return Optional.empty();
		}
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	protected ImmutableValue<?> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY, getValue()).asImmutable();
	}

	@Override
	public EntityData asMutable() {
		return new EntityData(this.getValue());
	}
}
