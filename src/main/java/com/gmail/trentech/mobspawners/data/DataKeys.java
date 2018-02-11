package com.gmail.trentech.mobspawners.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

import com.gmail.trentech.mobspawners.data.serializable.EntityModule;
import com.gmail.trentech.mobspawners.data.serializable.QuantityModule;
import com.gmail.trentech.mobspawners.data.serializable.Spawner;
import com.gmail.trentech.mobspawners.data.serializable.SpeedModule;
import com.google.common.reflect.TypeToken;

public class DataKeys {

	private static TypeToken<Value<Spawner>> spawnerToken = new TypeToken<Value<Spawner>>() {
		private static final long serialVersionUID = -4292651823337198660L;
	};	
	private static TypeToken<Value<EntityModule>> entityToken = new TypeToken<Value<EntityModule>>() {
		private static final long serialVersionUID = -1361337362417931680L;
	};
	private static TypeToken<Value<QuantityModule>> quantityToken = new TypeToken<Value<QuantityModule>>() {
		private static final long serialVersionUID = 5086876631599795947L;
	};
	private static TypeToken<Value<SpeedModule>> speedToken = new TypeToken<Value<SpeedModule>>() {
		private static final long serialVersionUID = -6858645372853382408L;
	};
	
	public static Key<Value<Spawner>> SPAWNER_DATA = Key.builder().type(spawnerToken).id("spawner").name("spawner").query(DataQuery.of("spawner")).build();
	public static Key<Value<EntityModule>> ENTITY_MODULE = Key.builder().type(entityToken).id("entity_module").name("entitymodule").query(DataQuery.of("entity_module")).build();
	public static Key<Value<QuantityModule>> QUANTITY_MODULE = Key.builder().type(quantityToken).id("quantity_module").name("quantitymodule").query(DataQuery.of("quantity_module")).build();
	public static Key<Value<SpeedModule>> SPEED_MODULE = Key.builder().type(speedToken).id("speed_module").name("speedmodule").query(DataQuery.of("speed_module")).build();
}
