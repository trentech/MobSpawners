package com.gmail.trentech.mobspawners.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityArchetype;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.google.common.reflect.TypeToken;

public class DataKeys {

	private static TypeToken<Value<Spawner>> spawnerToken = new TypeToken<Value<Spawner>>() {
				private static final long serialVersionUID = -1;
	};
	
	private static TypeToken<Value<EntityArchetype>> entityToken = new TypeToken<Value<EntityArchetype>>() {
		private static final long serialVersionUID = -1;
	};
	
	public static Key<Value<Spawner>> SPAWNER_DATA = Key.builder().type(spawnerToken).id("spawner_data").name("spawner_data").query(DataQuery.of("spawner_data")).build();
	public static Key<Value<EntityArchetype>> ENTITY_DATA = Key.builder().type(entityToken).id("entity_data").name("entity_data").query(DataQuery.of("entity_data")).build();
}
