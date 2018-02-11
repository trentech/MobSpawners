package com.gmail.trentech.mobspawners.data.serializable;

import static org.spongepowered.api.data.DataQuery.of;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.EntityArchetype;

public class EntityModule implements DataSerializable {

	protected static final DataQuery ENTITY = of("entity");
	protected static final DataQuery ID = of("id");
	
	protected Optional<EntityArchetype> entity = Optional.empty();
	protected UUID id = UUID.randomUUID();

	public EntityModule(EntityArchetype entity) {
		this.entity = Optional.of(entity);
	}

	public EntityModule() {}

	public Optional<EntityArchetype> getEntity() {
		return entity;
	}

	public void setEntity(Optional<EntityArchetype> entity) {
		this.entity = entity;
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = DataContainer.createNew().set(ID, DataTranslators.UUID.translate(id));

		if (entity.isPresent()) {
			container.set(ENTITY, entity.get());
		}

		return container;
	}

	public static class Builder extends AbstractDataBuilder<EntityModule> {

		public Builder() {
			super(EntityModule.class, 1);
		}

		@Override
		protected Optional<EntityModule> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(ENTITY)) {
				EntityArchetype entity = EntityArchetype.builder().build(container.getView(ENTITY).get()).get();
				
				return Optional.of(new EntityModule(entity));
			}

			return Optional.of(new EntityModule());
		}
	}
}
