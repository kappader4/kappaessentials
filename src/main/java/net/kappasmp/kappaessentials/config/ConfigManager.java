package net.kappasmp.kappaessentials.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;
    private static ModConfigWrapper config = new ModConfigWrapper(); // holds .homes

    public static void init(Path configDir) {
        configFile = configDir.resolve("Kappaessentials/config.json").toFile();
        if (!configFile.exists()) {
            save(); // Generate default config
        }
        load(); // Then load
    }

    public static void load() {
        try (FileReader reader = new FileReader(configFile)) {
            ModConfigWrapper loaded = GSON.fromJson(reader, ModConfigWrapper.class);
            if (loaded != null && loaded.homes != null) {
                config = loaded;
                System.out.println("[Kappaessentials] Loaded config: " + GSON.toJson(config));
            } else {
                System.err.println("[Kappaessentials] Failed to load config — using defaults.");
            }
        } catch (Exception e) {
            System.err.println("[Kappaessentials] Failed to read config.json: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception e) {
            System.err.println("[Kappaessentials] Failed to save config.json: " + e.getMessage());
        }
    }

    public static HomeConfig getHomeConfig() {
        return config.homes;
    }
}
