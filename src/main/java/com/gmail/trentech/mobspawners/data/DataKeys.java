package com.gmail.trentech.mobspawners.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityArchetype;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.google.common.reflect.TypeToken;

public class DataKeys {

	private static TypeToken<Value<Spawner>> spawnerToken = new TypeToken<Value<Spawner>>() {
				private static final long serialVersionUID = -6048452336902928437L;
	};
	
	private static TypeToken<Value<EntityArchetype>> entityToken = new TypeToken<Value<EntityArchetype>>() {
		private static final long serialVersionUID = -1973282741282076889L;
	};
	
	public static Key<Value<Spawner>> SPAWNER_DATA = Key.builder().type(spawnerToken).id("mobspawners:spawnerdata").name("SpawnerData").query(DataQuery.of('.', "spawnerdata")).build();
	public static Key<Value<EntityArchetype>> ENTITY_ARCHE = Key.builder().type(entityToken).id("mobspawners:entityarchetype").name("Entity").query(DataQuery.of('.', "entityarchetype")).build();
}
