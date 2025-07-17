package net.kappasmp.kappaessentials.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {

    private static Plugin plugin;
    private static File configFile;
    private static FileConfiguration configYaml;
    private static ModConfigWrapper config = new ModConfigWrapper();

    public static void init(Plugin owningPlugin, Path configDir) {
        plugin = owningPlugin;

        File pluginFolder = configDir.resolve("KappaEssentials").toFile();
        if (!pluginFolder.exists() && !pluginFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create plugin config folder!");
        }

        configFile = new File(pluginFolder, "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile(); // create empty file
                save(); // fill with defaults
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create config.yml: " + e.getMessage());
            }
        }

        load();
    }

    public static void load() {
        configYaml = YamlConfiguration.loadConfiguration(configFile);

        // Populate Java config object from YAML values
        HomeConfig homeConfig = new HomeConfig();
        homeConfig.homeLimit = configYaml.getInt("homes.homeLimit", 3);
        homeConfig.cooldownSeconds = configYaml.getInt("homes.cooldownSeconds", 10);
        homeConfig.crossDimension = configYaml.getBoolean("homes.crossDimension", false);

        config.homes = homeConfig;
        plugin.getLogger().info("[KappaEssentials] config.yml loaded.");
    }

    public static void save() {
        if (configYaml == null) configYaml = new YamlConfiguration();

        configYaml.set("homes.homeLimit", config.homes.homeLimit);
        configYaml.set("homes.cooldownSeconds", config.homes.cooldownSeconds);
        configYaml.set("homes.crossDimension", config.homes.crossDimension);

        try {
            configYaml.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save config.yml: " + e.getMessage());
        }
    }

    public static HomeConfig getHomeConfig() {
        return config.homes;
    }
}
