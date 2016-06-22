package com.gmail.trentech.customspawners.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.translator.ConfigurateTranslator;

import com.gmail.trentech.customspawners.Main;
import com.gmail.trentech.customspawners.data.spawner.Spawner;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class Serializer {

	public static String serialize(Spawner spawner) {
		ConfigurationNode node = ConfigurateTranslator.instance().translateData(spawner.toContainer());

		StringWriter stringWriter = new StringWriter();
		try {
			HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringWriter.toString();
	}

	public static Spawner deserialize(String item) {
		ConfigurationNode node = null;
		try {
			node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ConfigurateTranslator translator = ConfigurateTranslator.instance();
		DataManager manager = Main.getGame().getDataManager();

		DataView dataView = translator.translateFrom(node);

		return manager.deserialize(Spawner.class, dataView).get();
	}
}
