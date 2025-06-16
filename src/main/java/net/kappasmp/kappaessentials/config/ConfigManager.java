package net.kappasmp.kappaessentials.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
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

                if (homeMap.containsKey("teleportDelaySeconds"))
                    homeConfig.teleportDelaySeconds = (int) homeMap.get("teleportDelaySeconds");
                if (homeMap.containsKey("cancelOnMove"))
                    homeConfig.cancelOnMove = (boolean) homeMap.get("cancelOnMove");
                if (homeMap.containsKey("cooldownSeconds"))
                    homeConfig.cooldownSeconds = (int) homeMap.get("cooldownSeconds");
                if (homeMap.containsKey("crossDimensionAllowed"))
                    homeConfig.crossDimensionAllowed = (boolean) homeMap.get("crossDimensionAllowed");
                if (homeMap.containsKey("showCountdownMessages"))
                    homeConfig.showCountdownMessages = (boolean) homeMap.get("showCountdownMessages");

                config.homes = homeConfig;
                System.out.println("[KappaEssentials] Loaded config.yml");
            } else {
                System.err.println("[KappaEssentials] Invalid config.yml, using defaults.");
            }

        } catch (Exception e) {
            System.err.println("[KappaEssentials] Failed to read config.yml: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            configFile.getParentFile().mkdirs();

            Map<String, Object> homeMap = new HashMap<>();
            homeMap.put("teleportDelaySeconds", config.homes.teleportDelaySeconds);
            homeMap.put("cancelOnMove", config.homes.cancelOnMove);
            homeMap.put("cooldownSeconds", config.homes.cooldownSeconds);
            homeMap.put("crossDimensionAllowed", config.homes.crossDimensionAllowed);
            homeMap.put("showCountdownMessages", config.homes.showCountdownMessages);

            Map<String, Object> rootMap = new HashMap<>();
            rootMap.put("homes", homeMap);

            try (Writer writer = new FileWriter(configFile)) {
                YAML.dump(rootMap, writer);
            }

        } catch (Exception e) {
            System.err.println("[KappaEssentials] Failed to save config.yml: " + e.getMessage());
        }
    }

    public static HomeConfig getHomeConfig() {
        return config.homes;
    }
}
