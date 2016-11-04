package com.gmail.trentech.mobspawners.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityArchetype;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.google.common.reflect.TypeToken;

public class Keys {

	private static final TypeToken<Value<Spawner>> VALUE_SPAWNER = new TypeToken<Value<Spawner>>() {
		private static final long serialVersionUID = 395242399877312340L;
	};
	
	private static final TypeToken<Spawner> SPAWNER_TOKEN = new TypeToken<Spawner>() {
		private static final long serialVersionUID = -8726734755833911770L;
	};
	
	public static final Key<Value<Spawner>> SPAWNER = KeyFactory.makeSingleKey(SPAWNER_TOKEN, VALUE_SPAWNER, DataQuery.of("spawner"), "mobspawners:spawner", "spawner");
	
	private static final TypeToken<Value<EntityArchetype>> VALUE_ENTITY = new TypeToken<Value<EntityArchetype>>() {
		private static final long serialVersionUID = 3763780298608964109L;	
	};
	
	private static final TypeToken<EntityArchetype> ENTITY_TOKEN = new TypeToken<EntityArchetype>() {
		private static final long serialVersionUID = 1441551829930777880L;
	};
	
	public static final Key<Value<EntityArchetype>> ENTITY = KeyFactory.makeSingleKey(ENTITY_TOKEN, VALUE_ENTITY, DataQuery.of("entity"), "mobspawners:entity", "entity");

}
