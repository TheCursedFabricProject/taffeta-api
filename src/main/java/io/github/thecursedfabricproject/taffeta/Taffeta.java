package io.github.thecursedfabricproject.taffeta;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class Taffeta implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Taffeta");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Loading Taffeta API");
	}
}
