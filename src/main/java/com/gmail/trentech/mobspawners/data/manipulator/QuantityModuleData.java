package com.gmail.trentech.mobspawners.data.manipulator;

import static com.gmail.trentech.mobspawners.data.DataKeys.QUANTITY_MODULE;

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

import com.gmail.trentech.mobspawners.data.manipulator.QuantityModuleData.Immutable;
import com.gmail.trentech.mobspawners.data.serializable.QuantityModule;

public class QuantityModuleData extends AbstractSingleData<QuantityModule, QuantityModuleData, Immutable> {

	public QuantityModuleData() {
		super(new QuantityModule(), QUANTITY_MODULE);
	}

	public QuantityModuleData(QuantityModule value) {
		super(value, QUANTITY_MODULE);
	}

	public Value<QuantityModule> quantityModule() {
		return Sponge.getRegistry().getValueFactory().createValue(QUANTITY_MODULE, getValue());
	}

	@Override
	protected Value<QuantityModule> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(QUANTITY_MODULE, getValue());
	}
	
	@Override
	public QuantityModuleData copy() {
		return new QuantityModuleData(this.getValue());
	}

	@Override
	public Optional<QuantityModuleData> fill(DataHolder dataHolder, MergeFunction mergeFunction) {
		Optional<QuantityModuleData> optionalData = dataHolder.get(QuantityModuleData.class);
		
        if (optionalData.isPresent()) {
        	QuantityModuleData data = optionalData.get();
        	QuantityModuleData finalData = mergeFunction.merge(this, data);
            setValue(finalData.getValue());
        }
        
        return Optional.of(this);
	}

	@Override
	public Optional<QuantityModuleData> from(DataContainer container) {
		if (container.contains(QUANTITY_MODULE.getQuery())) {
			return Optional.of(set(QUANTITY_MODULE, container.getSerializable(QUANTITY_MODULE.getQuery(), QuantityModule.class).orElse(getValue())));
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
		return DataContainer.createNew().set(QUANTITY_MODULE, getValue());
	}

	public static class Immutable extends AbstractImmutableSingleData<QuantityModule, Immutable, QuantityModuleData> {

		protected Immutable(QuantityModule value) {
			super(value, QUANTITY_MODULE);
		}

		public ImmutableValue<QuantityModule> entity() {
			return Sponge.getRegistry().getValueFactory().createValue(QUANTITY_MODULE, getValue()).asImmutable();
		}

		@Override
		protected ImmutableValue<QuantityModule> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(QUANTITY_MODULE, getValue()).asImmutable();
		}

		@Override
		public QuantityModuleData asMutable() {
			return new QuantityModuleData(getValue());
		}
		
		@Override
		public int getContentVersion() {
			return 1;
		}
	}
	
	public static class Builder extends AbstractDataBuilder<QuantityModuleData> implements DataManipulatorBuilder<QuantityModuleData, Immutable> {

		public Builder() {
			super(QuantityModuleData.class, 1);
		}

		@Override
		protected Optional<QuantityModuleData> buildContent(DataView container) throws InvalidDataException {
			if (!container.contains(QUANTITY_MODULE.getQuery())) {
				return Optional.empty();
			}
			QuantityModule spawner = container.getSerializable(QUANTITY_MODULE.getQuery(), QuantityModule.class).get();

			return Optional.of(new QuantityModuleData(spawner));
		}

		@Override
		public QuantityModuleData create() {
			return new QuantityModuleData();
		}

		@Override
		public Optional<QuantityModuleData> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}
	}
}
