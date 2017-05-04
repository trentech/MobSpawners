package com.gmail.trentech.mobspawners.data.spawner;

import static com.gmail.trentech.mobspawners.data.DataKeys.SPAWNER_DATA;

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

import com.gmail.trentech.mobspawners.data.spawner.SpawnerData.Immutable;

public class SpawnerData extends AbstractSingleData<Spawner, SpawnerData, Immutable> {

	protected SpawnerData() {
		super(new Spawner(), SPAWNER_DATA);
	}

	public SpawnerData(Spawner value) {
		super(value, SPAWNER_DATA);
	}

	public Value<Spawner> spawner() {
		return Sponge.getRegistry().getValueFactory().createValue(SPAWNER_DATA, getValue());
	}

	@Override
	protected Value<Spawner> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(SPAWNER_DATA, getValue());
	}
	
	@Override
	public SpawnerData copy() {
		return new SpawnerData(this.getValue());
	}

	@Override
	public Optional<SpawnerData> fill(DataHolder dataHolder, MergeFunction mergeFunction) {
		Optional<SpawnerData> optionalData = dataHolder.get(SpawnerData.class);
		
        if (optionalData.isPresent()) {
        	SpawnerData data = optionalData.get();
        	SpawnerData finalData = mergeFunction.merge(this, data);
            setValue(finalData.getValue());
        }
        
        return Optional.of(this);
	}

	@Override
	public Optional<SpawnerData> from(DataContainer container) {
		if (container.contains(SPAWNER_DATA.getQuery())) {
			return Optional.of(set(SPAWNER_DATA, container.getSerializable(SPAWNER_DATA.getQuery(), Spawner.class).orElse(getValue())));
		}
		return Optional.empty();
	}

	@Override
	public Immutable asImmutable() {
		return new Immutable(getValue());
	}

	@Override
	public int getContentVersion() {
		return 1;
	}
	
	@Override
	public DataContainer toContainer() {
		return DataContainer.createNew().set(SPAWNER_DATA, getValue());
	}

	public static class Immutable extends AbstractImmutableSingleData<Spawner, Immutable, SpawnerData> {

		protected Immutable(Spawner value) {
			super(value, SPAWNER_DATA);
		}

		public ImmutableValue<Spawner> entity() {
			return Sponge.getRegistry().getValueFactory().createValue(SPAWNER_DATA, getValue()).asImmutable();
		}

		@Override
		protected ImmutableValue<Spawner> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(SPAWNER_DATA, getValue()).asImmutable();
		}

		@Override
		public SpawnerData asMutable() {
			return new SpawnerData(getValue());
		}
		
		@Override
		public int getContentVersion() {
			return 1;
		}
	}
	
	public static class Builder extends AbstractDataBuilder<SpawnerData> implements DataManipulatorBuilder<SpawnerData, Immutable> {

		public Builder() {
			super(SpawnerData.class, 1);
		}

		@Override
		protected Optional<SpawnerData> buildContent(DataView container) throws InvalidDataException {
			if (!container.contains(SPAWNER_DATA.getQuery())) {
				return Optional.empty();
			}
			Spawner spawner = container.getSerializable(SPAWNER_DATA.getQuery(), Spawner.class).get();

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
	}
}
