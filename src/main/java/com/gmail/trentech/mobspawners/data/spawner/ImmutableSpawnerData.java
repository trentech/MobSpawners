package com.gmail.trentech.mobspawners.data.spawner;

import static com.gmail.trentech.mobspawners.data.Keys.SPAWNER;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableSpawnerData extends AbstractImmutableSingleData<Spawner, ImmutableSpawnerData, SpawnerData> {

	protected ImmutableSpawnerData(Spawner value) {
		super(value, SPAWNER);
	}

	public ImmutableValue<Spawner> transmitter() {
		return Sponge.getRegistry().getValueFactory().createValue(SPAWNER, getValue(), getValue()).asImmutable();
	}

	@Override
	public <E> Optional<ImmutableSpawnerData> with(Key<? extends BaseValue<E>> key, E value) {
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
		return Sponge.getRegistry().getValueFactory().createValue(SPAWNER, getValue()).asImmutable();
	}

	@Override
	public SpawnerData asMutable() {
		return new SpawnerData(this.getValue());
	}
}
