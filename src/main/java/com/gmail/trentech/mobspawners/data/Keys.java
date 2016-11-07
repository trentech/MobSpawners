package com.gmail.trentech.mobspawners.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.EntityArchetype;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;
import com.google.common.reflect.TypeToken;

public class Keys {

	public static Key<Value<Spawner>> SPAWNER_DATA = KeyFactory.makeSingleKey(TypeToken.of(Spawner.class),
            new TypeToken<Value<Spawner>>() {
				private static final long serialVersionUID = -6048452336902928437L;},
            DataQuery.of('.', "spawnerdata"), "mobspawners:spawnerdata", "SpawnerData"
    );
	
	public static Key<Value<EntityArchetype>> ENTITY_ARCHE = KeyFactory.makeSingleKey(TypeToken.of(EntityArchetype.class),
            new TypeToken<Value<EntityArchetype>>() {
				private static final long serialVersionUID = -1973282741282076889L;},
            DataQuery.of('.', "entityarchetype"), "mobspawners:entityarchetype", "EntityArchetype"
    );

}
