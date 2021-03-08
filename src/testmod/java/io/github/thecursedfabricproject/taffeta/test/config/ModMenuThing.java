package io.github.thecursedfabricproject.taffeta.test.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import io.github.thecursedfabricproject.taffeta.config.TaffetaConfig;
import io.github.thecursedfabricproject.taffeta.test.config.ConfigTestMod.TestConfig;

public class ModMenuThing implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> TaffetaConfig.getConfig(TestConfig.class, "taffeta-test").getScreen(parent);
    }
}
