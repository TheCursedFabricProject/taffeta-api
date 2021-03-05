package io.github.thecursedfabricproject.taffeta.test.config;

import io.github.coolmineman.coolconfig.Config;
import io.github.coolmineman.coolconfig.annotation.Comment;
import io.github.thecursedfabricproject.taffeta.config.TaffetaConfig;
import net.fabricmc.api.ModInitializer;

public class ConfigTestMod implements ModInitializer {
    TestConfig config;

    @Override
    public void onInitialize() {
        config = TaffetaConfig.getConfig(TestConfig.class, "taffeta-test");
        System.out.println("Epic Amount: " + config.epic_amount());
    }

    public interface TestConfig extends Config {
        @Comment("The epic amount of this config")
        default int epic_amount() {
            return 100;
        }
    }
}
