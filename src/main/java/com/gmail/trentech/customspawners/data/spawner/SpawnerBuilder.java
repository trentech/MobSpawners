package com.gmail.trentech.customspawners.data.spawner;

import static com.gmail.trentech.customspawners.data.DataQueries.ENTITIES;
import static com.gmail.trentech.customspawners.data.DataQueries.LOCATION;
import static com.gmail.trentech.customspawners.data.DataQueries.AMOUNT;
import static com.gmail.trentech.customspawners.data.DataQueries.TIME;
import static com.gmail.trentech.customspawners.data.DataQueries.RANGE;
import static com.gmail.trentech.customspawners.data.DataQueries.ENABLE;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class SpawnerBuilder extends AbstractDataBuilder<Spawner> {

    public SpawnerBuilder() {
        super(Spawner.class, 2);
    }

    @Override
    protected Optional<Spawner> buildContent(DataView container) throws InvalidDataException {
        if (container.contains(ENTITIES, LOCATION, AMOUNT, TIME, RANGE, ENABLE)) {
        	@SuppressWarnings("unchecked")
			List<String> entities = (List<String>) container.getList(ENTITIES).get();
        	String location = container.getString(LOCATION).get();
        	int amount = container.getInt(AMOUNT).get();
        	int time = container.getInt(TIME).get();
        	int range = container.getInt(RANGE).get();
        	boolean enable = container.getBoolean(ENABLE).get();
        	
            return Optional.of(new Spawner(entities, location, amount, time, range, enable));
        }
        
        return Optional.empty();
    }
}
