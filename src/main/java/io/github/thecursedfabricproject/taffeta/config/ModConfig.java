package io.github.thecursedfabricproject.taffeta.config;

import io.github.coolmineman.coolconfig.Config;
import io.github.coolmineman.coolconfig.annotation.NotConfigValue;
import io.github.thecursedfabricproject.taffeta.config.impl.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

public interface ModConfig extends Config {
    @Environment(EnvType.CLIENT)
    @NotConfigValue
    default Screen getScreen(Screen parent) {
        return new ConfigScreen(parent, this);
    }

    @NotConfigValue
    default boolean save() {
        String modid = TaffetaConfig.MODID_LOOKUP.get(this);
        return TaffetaConfig.trySaveConfig(TaffetaConfig.getConfigPath(modid), this, modid);
    }
}
