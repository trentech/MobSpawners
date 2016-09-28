package com.gmail.trentech.mobspawners.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

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

}
