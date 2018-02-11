package com.gmail.trentech.mobspawners.data.serializable;

import static org.spongepowered.api.data.DataQuery.of;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import com.gmail.trentech.mobspawners.Main;
import com.gmail.trentech.pjc.core.ConfigManager;

public class QuantityModule implements DataSerializable {

	protected static final DataQuery QUANTITY = of("quantity");
	protected static final DataQuery ID = of("id");
	
	protected int quantity = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "quantity-module-increment").getInt();
	protected String id = UUID.randomUUID().toString();

	private QuantityModule(int quantity) {
		this.quantity = quantity;
	}

	public QuantityModule() {}

	public int getQuantity() {
		return quantity;
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		return DataContainer.createNew().set(ID, id).set(QUANTITY, quantity);
	}

	public static class Builder extends AbstractDataBuilder<QuantityModule> {

		public Builder() {
			super(QuantityModule.class, 1);
		}

		@Override
		protected Optional<QuantityModule> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(QUANTITY)) {
				int quantity = container.getInt(QUANTITY).get();			
				return Optional.of(new QuantityModule(quantity));
			}

			return Optional.empty();
		}
	}
}
