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

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.pjc.core.ConfigManager;

public class SpeedModule implements DataSerializable {

	protected static final DataQuery SPEED = of("speed");
	protected static final DataQuery ID = of("id");
	
	protected int speed = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "quantity-module-increment").getInt();
	protected UUID id = UUID.randomUUID();

	private SpeedModule(int speed) {
		this.speed = speed;
	}

	public SpeedModule() {}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		return DataContainer.createNew().set(ID, DataTranslators.UUID.translate(id)).set(SPEED, speed);
	}

	public static class Builder extends AbstractDataBuilder<SpeedModule> {

		public Builder() {
			super(SpeedModule.class, 1);
		}

		@Override
		protected Optional<SpeedModule> buildContent(DataView container) throws InvalidDataException {
			return Optional.of(new SpeedModule(container.getInt(SPEED).get()));
		}
	}
}
