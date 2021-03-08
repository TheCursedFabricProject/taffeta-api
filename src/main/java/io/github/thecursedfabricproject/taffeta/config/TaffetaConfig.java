package io.github.thecursedfabricproject.taffeta.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.jetbrains.annotations.ApiStatus.Experimental;

import io.github.coolmineman.coolconfig.Config;
import io.github.coolmineman.coolconfig.CoolConfig;
import io.github.coolmineman.coolconfig.CoolConfigNt;
import io.github.coolmineman.nestedtext.api.NestedTextReader;
import io.github.coolmineman.nestedtext.api.NestedTextWriter;
import io.github.coolmineman.nestedtext.api.tree.NestedTextNode;
import io.github.thecursedfabricproject.taffeta.Taffeta;
import io.github.thecursedfabricproject.taffeta.util.IoUtil;
import net.fabricmc.loader.api.FabricLoader;

@Experimental
public final class TaffetaConfig {
    private TaffetaConfig() { }

    private static final HashMap<String, ModConfig> CONFIGS = new HashMap<>();
    static final HashMap<ModConfig, String> MODID_LOOKUP = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends ModConfig> T getConfig(Class<T> clazz, String modid) {
        return (T) CONFIGS.computeIfAbsent(modid, id -> {
            ModConfig config = CoolConfig.create(clazz);
            Path path = getConfigPath(modid);
            if (Files.exists(path)) {
                tryLoadConfig(path, config, modid);
            } else {
                trySaveConfig(path, config, modid);
            }
            MODID_LOOKUP.put(config, modid);
            return config;
        });
    }

    static Path getConfigPath(String modid) {
        return FabricLoader.getInstance().getConfigDir().resolve(modid + ".nt");
    }

    private static boolean tryLoadConfig(Path path, Config config, String modid) {
        if (!Files.isRegularFile(path)) {
            error(modid, "config is not regular file");
            return false;
        }
        try {
            NestedTextNode node;
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                node = NestedTextReader.read(reader);
            }
            CoolConfigNt.load(config, node);
        } catch (Exception e) {
            error(modid, "got exception");
            Taffeta.LOGGER.error(e);
            return false;
        }
        return true;
    }

    static boolean trySaveConfig(Path path, Config config, String modid) {
        try {
            Path tmpFile = Files.createTempFile(path.getParent(), modid, ".nt");
            NestedTextNode node = CoolConfigNt.save(config);
            try (BufferedWriter writer = Files.newBufferedWriter(tmpFile)) {
                NestedTextWriter.write(node, writer);
            }
            IoUtil.replaceFile(tmpFile, path);
        } catch (Exception e) {
            Taffeta.LOGGER.error("Error saving config for modid {}", modid);
            Taffeta.LOGGER.error(e);
            return false;
        }
        return true;
    }

    private static void error(String modid, String message) {
        Taffeta.LOGGER.error("Error loading config for modid {}, {}", modid, message);
    }
}
