package com.luckyblock;

import com.luckyblock.block.ModBlocks;

import net.fabricmc.api.ModInitializer;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuckyBlockInitializer implements ModInitializer {
	public static final String MOD_ID = "luckyblock";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Flag to avoid initializing Bootstrap multiple times
    private static boolean isBootstrapInitialized = false;

    /**
     * Ensures the Minecraft Bootstrap is initialized.
     * This is required for proper registry access in 1.21.10.
     */
    private void ensureBootstrapInitialized() {
        if (!isBootstrapInitialized) {
            try {
                SharedConstants.createGameVersion();
                Bootstrap.initialize();
                isBootstrapInitialized = true;
            } catch (Exception e) {
                LOGGER.warn("Bootstrap initialization failed: {}", e.getMessage());
                // Continue anyway - it might already be initialized
            }
        }
    }

	@Override
	public void onInitialize() {

        ensureBootstrapInitialized();

        LuckyCommandsInitializer.registerCommands();
        ModBlocks.registerModBlocks();

        LOGGER.info(MOD_ID + " Mod initialized successfully!");
	}
}