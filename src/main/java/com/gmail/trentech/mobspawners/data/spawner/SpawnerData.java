package com.gmail.trentech.mobspawners.data.spawner;

import static com.gmail.trentech.mobspawners.data.Keys.SPAWNER;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import com.google.common.base.Preconditions;

public class SpawnerData extends AbstractSingleData<Spawner, SpawnerData, ImmutableSpawnerData> {

	protected SpawnerData() {
		super(new Spawner(), SPAWNER);
	}

	public SpawnerData(Spawner value) {
		super(value, SPAWNER);
	}

	public Value<Spawner> spawner() {
		return Sponge.getRegistry().getValueFactory().createValue(SPAWNER, getValue(), getValue());
	}

	@Override
	public SpawnerData copy() {
		return new SpawnerData(this.getValue());
	}

	@Override
	public Optional<SpawnerData> fill(DataHolder dataHolder, MergeFunction mergeFn) {
		SpawnerData signData = Preconditions.checkNotNull(mergeFn).merge(copy(), dataHolder.get(SpawnerData.class).orElse(copy()));
		return Optional.of(set(SPAWNER, signData.get(SPAWNER).get()));
	}

	@Override
	public Optional<SpawnerData> from(DataContainer container) {
		if (container.contains(SPAWNER.getQuery())) {
			return Optional.of(set(SPAWNER, container.getSerializable(SPAWNER.getQuery(), Spawner.class).orElse(getValue())));
		}
		return Optional.empty();
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public ImmutableSpawnerData asImmutable() {
		return new ImmutableSpawnerData(this.getValue());
	}

	@Override
	protected Value<Spawner> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(SPAWNER, getValue(), getValue());
	}

	@Override
	public DataContainer toContainer() {
		return new MemoryDataContainer().set(SPAWNER, getValue());
	}

}