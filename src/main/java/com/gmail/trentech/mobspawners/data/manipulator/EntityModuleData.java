package com.gmail.trentech.mobspawners.data.manipulator;

import static com.gmail.trentech.mobspawners.data.DataKeys.ENTITY_MODULE;

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

import com.gmail.trentech.mobspawners.data.manipulator.EntityModuleData.Immutable;
import com.gmail.trentech.mobspawners.data.serializable.EntityModule;

public class EntityModuleData extends AbstractSingleData<EntityModule, EntityModuleData, Immutable> {

	public EntityModuleData() {
		super(new EntityModule(), ENTITY_MODULE);
	}

	public EntityModuleData(EntityModule value) {
		super(value, ENTITY_MODULE);
	}

	public Value<EntityModule> entityModule() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY_MODULE, getValue());
	}

	@Override
	protected Value<EntityModule> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(ENTITY_MODULE, getValue());
	}
	
	@Override
	public EntityModuleData copy() {
		return new EntityModuleData(this.getValue());
	}

	@Override
	public Optional<EntityModuleData> fill(DataHolder dataHolder, MergeFunction mergeFunction) {
		Optional<EntityModuleData> optionalData = dataHolder.get(EntityModuleData.class);
		
        if (optionalData.isPresent()) {
        	EntityModuleData data = optionalData.get();
        	EntityModuleData finalData = mergeFunction.merge(this, data);
            setValue(finalData.getValue());
        }
        
        return Optional.of(this);
	}

	@Override
	public Optional<EntityModuleData> from(DataContainer container) {
		if (container.contains(ENTITY_MODULE.getQuery())) {
			return Optional.of(set(ENTITY_MODULE, container.getSerializable(ENTITY_MODULE.getQuery(), EntityModule.class).orElse(getValue())));
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
		return DataContainer.createNew().set(ENTITY_MODULE, getValue());
	}

	public static class Immutable extends AbstractImmutableSingleData<EntityModule, Immutable, EntityModuleData> {

		protected Immutable(EntityModule value) {
			super(value, ENTITY_MODULE);
		}

		public ImmutableValue<EntityModule> entity() {
			return Sponge.getRegistry().getValueFactory().createValue(ENTITY_MODULE, getValue()).asImmutable();
		}

		@Override
		protected ImmutableValue<EntityModule> getValueGetter() {
			return Sponge.getRegistry().getValueFactory().createValue(ENTITY_MODULE, getValue()).asImmutable();
		}

		@Override
		public EntityModuleData asMutable() {
			return new EntityModuleData(getValue());
		}
		
		@Override
		public int getContentVersion() {
			return 1;
		}
	}
	
	public static class Builder extends AbstractDataBuilder<EntityModuleData> implements DataManipulatorBuilder<EntityModuleData, Immutable> {

		public Builder() {
			super(EntityModuleData.class, 1);
		}

		@Override
		protected Optional<EntityModuleData> buildContent(DataView container) throws InvalidDataException {
			if (!container.contains(ENTITY_MODULE.getQuery())) {
				return Optional.empty();
			}
			EntityModule spawner = container.getSerializable(ENTITY_MODULE.getQuery(), EntityModule.class).get();

			return Optional.of(new EntityModuleData(spawner));
		}

		@Override
		public EntityModuleData create() {
			return new EntityModuleData();
		}

		@Override
		public Optional<EntityModuleData> createFrom(DataHolder dataHolder) {
			return create().fill(dataHolder);
		}
	}
}
