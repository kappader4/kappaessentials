package net.kappasmp.kappaessentials.model;

import net.kappasmp.kappaessentials.config.HomeConfig;
import net.kappasmp.kappaessentials.config.ModConfigWrapper;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class ConfigManager {

    private static final Yaml YAML;
    private static File configFile;
    private static ModConfigWrapper config = new ModConfigWrapper(); // holds .homes

    static {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        YAML = new Yaml(options);
    }

    public static void init(JavaPlugin plugin) {
        File configDir = plugin.getDataFolder();
        configFile = new File(configDir, "config.yml");

        if (!configFile.exists()) {
            save(); // Generate default config
        }

        load(); // Then load it into memory
    }

    @SuppressWarnings("unchecked")
    public static void load() {
        try (InputStream input = new FileInputStream(configFile)) {
            Map<String, Object> data = YAML.load(input);

            if (data != null && data.containsKey("homes")) {
                Map<String, Object> homeMap = (Map<String, Object>) data.get("homes");
                HomeConfig homeConfig = new HomeConfig();

                // Example manual value extraction
                if (homeMap.containsKey("teleportDelaySeconds")) {
                    homeConfig.teleportDelaySeconds = (int) homeMap.get("teleportDelaySeconds");
                }
                if (homeMap.containsKey("cancelOnMove")) {
                    homeConfig.cancelOnMove = (boolean) homeMap.get("cancelOnMove");
                }
                if (homeMap.containsKey("cooldownSeconds")) {
                    homeConfig.cooldownSeconds = (int) homeMap.get("cooldownSeconds");
                }
                if (homeMap.containsKey("crossDimensionAllowed")) {
                    homeConfig.crossDimensionAllowed = (boolean) homeMap.get("crossDimensionAllowed");
                }
                if (homeMap.containsKey("showCountdownMessages")) {
                    homeConfig.showCountdownMessages = (boolean) homeMap.get("showCountdownMessages");
                }

                config.homes = homeConfig;
                System.out.println("[KappaEssentials] Loaded config.yml");
            } else {
                System.err.println("[KappaEssentials] Missing or invalid config.yml structure, using defaults.");
            }

        } catch (Exception e) {
            System.err.println("[KappaEssentials] Failed to read config.yml: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            configFile.getParentFile().mkdirs();

            Map<String, Object> data = Map.of(
                    "homes", Map.of(
                            "teleportDelaySeconds", config.homes.teleportDelaySeconds,
                            "cancelOnMove", config.homes.cancelOnMove,
                            "cooldownSeconds", config.homes.cooldownSeconds,
                            "crossDimensionAllowed", config.homes.crossDimensionAllowed,
                            "showCountdownMessages", config.homes.showCountdownMessages
                    )
            );

            try (Writer writer = new FileWriter(configFile)) {
                YAML.dump(data, writer);
            }

        } catch (Exception e) {
            System.err.println("[KappaEssentials] Failed to save config.yml: " + e.getMessage());
        }
    }

    public static HomeConfig getHomeConfig() {
        return config.homes;
    }
}
