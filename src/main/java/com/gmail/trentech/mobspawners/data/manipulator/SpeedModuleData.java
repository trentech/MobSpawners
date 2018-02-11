package com.gmail.trentech.mobspawners.data.manipulator;

import static com.gmail.trentech.mobspawners.data.DataKeys.SPEED_MODULE;

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

import com.gmail.trentech.mobspawners.data.manipulator.SpeedModuleData.Immutable;
import com.gmail.trentech.mobspawners.data.serializable.SpeedModule;

public class SpeedModuleData extends AbstractSingleData<SpeedModule, SpeedModuleData, Immutable> {

	public SpeedModuleData() {
		super(new SpeedModule(), SPEED_MODULE);
	}

	public SpeedModuleData(SpeedModule value) {
		super(value, SPEED_MODULE);
	}

	public Value<SpeedModule> speedModule() {
		return Sponge.getRegistry().getValueFactory().createValue(SPEED_MODULE, getValue());
	}

	@Override
	protected Value<SpeedModule> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(SPEED_MODULE, getValue());
	}
	
	@Override
	public SpeedModuleData copy() {
		return new SpeedModuleData(this.getValue());
	}

	@Override
	public Optional<SpeedModuleData> fill(DataHolder dataHolder, MergeFunction mergeFunction) {
		Optional<SpeedModuleData> optionalData = dataHolder.get(SpeedModuleData.class);
		
        if (optionalData.isPresent()) {
        	SpeedModuleData data = optionalData.get();
        	SpeedModuleData finalData = mergeFunction.merge(this, data);
            setValue(finalData.getValue());
        }
        
        return Optional.of(this);
	}

	@Override
	public Optional<SpeedModuleData> from(DataContainer container) {
		if (container.contains(SPEED_MODULE.getQuery())) {
			return Optional.of(set(SPEED_MODULE, container.getSerializable(SPEED_MODULE.getQuery(), SpeedModule.class).orElse(getValue())));
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
		return DataContainer.createNew().set(SPEED_MODULE, getValue());
	}

	public static class Immutable extends AbstractImmutableSingleData<SpeedModule, Immutable, SpeedModuleData> {

		protected Immutable(SpeedModule value) {
			super(value, SPEED_MODULE);
		}

		public ImmutableValue<SpeedModule> entity() {
			return Sponge.getRegistry().getValueFactory().createValue(SPEED_MODULE, getValue()).asImmutable();
		}

		@Override
		protected ImmutableValue<SpeedModule> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(SPEED_MODULE, getValue()).asImmutable();
		}

		@Override
		public SpeedModuleData asMutable() {
			return new SpeedModuleData(getValue());
		}
		
		@Override
		public int getContentVersion() {
			return 1;
		}
	}
	
	public static class Builder extends AbstractDataBuilder<SpeedModuleData> implements DataManipulatorBuilder<SpeedModuleData, Immutable> {

		public Builder() {
			super(SpeedModuleData.class, 1);
		}

		@Override
		protected Optional<SpeedModuleData> buildContent(DataView container) throws InvalidDataException {
			if (!container.contains(SPEED_MODULE.getQuery())) {
				return Optional.empty();
			}
			SpeedModule spawner = container.getSerializable(SPEED_MODULE.getQuery(), SpeedModule.class).get();

			return Optional.of(new SpeedModuleData(spawner));
		}

		@Override
		public SpeedModuleData create() {
			return new SpeedModuleData();
		}

		@Override
		public Optional<SpeedModuleData> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}
	}
}
