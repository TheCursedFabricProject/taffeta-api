package io.github.thecursedfabricproject.taffeta.config;

import io.github.coolmineman.coolconfig.Config;
import io.github.thecursedfabricproject.taffeta.config.impl.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

public interface ModConfig extends Config {
    @Environment(EnvType.CLIENT)
    default Screen getScreen() {
        return new ConfigScreen();
    }
}
