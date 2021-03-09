package io.github.thecursedfabricproject.taffeta.test.config;

import java.util.HashMap;
import java.util.Map;

import io.github.coolmineman.coolconfig.annotation.Comment;
import io.github.thecursedfabricproject.taffeta.config.ModConfig;
import io.github.thecursedfabricproject.taffeta.config.TaffetaConfig;
import net.fabricmc.api.ModInitializer;

public class ConfigTestMod implements ModInitializer {
    TestConfig config;

    @Override
    public void onInitialize() {
        config = TaffetaConfig.getConfig(TestConfig.class, "taffeta-test");
        System.out.println("Epic Amount: " + config.epic_amount());
    }

    public interface TestConfig extends ModConfig {
        @Comment("The epic amount of this config")
        default int epic_amount() {
            return 100;
        }

        default Map<Integer, String> ir() {
            return Map.of(8, "bruh");
        }
    }
}
