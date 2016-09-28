package com.gmail.trentech.mobspawners.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;

import com.gmail.trentech.mobspawners.data.spawner.Spawner;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class Serializer {

	public static String serialize(Spawner spawner) {
		try {
			ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(spawner.toContainer());
			StringWriter stringWriter = new StringWriter();		
			HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);		
			return stringWriter.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Spawner deserialize(String item) {
		try {
			ConfigurationNode node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();			
			DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);
			return Sponge.getDataManager().deserialize(Spawner.class, dataView).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
