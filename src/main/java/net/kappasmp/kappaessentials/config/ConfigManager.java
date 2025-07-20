package net.kappasmp.kappaessentials.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfigManager {

    private static final int CURRENT_CONFIG_VERSION = 1;

    private static Plugin plugin;
    private static File configFile;
    private static FileConfiguration configYaml;
    private static ModConfigWrapper config = new ModConfigWrapper();

    public static void init(Plugin owningPlugin, Path configDir) {
        plugin = owningPlugin;

        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create plugin config folder!");
        }

        configFile = new File(pluginFolder, "config.yml");

        if (!configFile.exists()) {
            save(); // Create fresh config with defaults
        }

        load();
        checkVersionAndBackup();
    }

    public static void load() {
        configYaml = YamlConfiguration.loadConfiguration(configFile);

        // Homes
        HomeConfig homeConfig = new HomeConfig();
        homeConfig.homeLimit = configYaml.getInt("homes.homeLimit", 3);
        homeConfig.cooldownSeconds = configYaml.getInt("homes.cooldownSeconds", 10);
        homeConfig.crossDimension = configYaml.getBoolean("homes.crossDimension", false);
        config.homes = homeConfig;

        // Auction
        AuctionConfig auctionConfig = new AuctionConfig();
        auctionConfig.enabled = configYaml.getBoolean("auction.enabled", true);
        auctionConfig.listingLimit = configYaml.getInt("auction.listingLimit", 10);
        auctionConfig.notifySeller = configYaml.getBoolean("auction.notifySeller", true);
        config.auction = auctionConfig;
    }

    public static void save() {
        if (configYaml == null) configYaml = new YamlConfiguration();

        configYaml.set("config-version", CURRENT_CONFIG_VERSION);

        configYaml.set("homes.homeLimit", config.homes.homeLimit);
        configYaml.set("homes.cooldownSeconds", config.homes.cooldownSeconds);
        configYaml.set("homes.crossDimension", config.homes.crossDimension);

        configYaml.set("auction.enabled", config.auction.enabled);
        configYaml.set("auction.listingLimit", config.auction.listingLimit);
        configYaml.set("auction.notifySeller", config.auction.notifySeller);

        try {
            configYaml.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save config.yml: " + e.getMessage());
        }
    }

    private static void checkVersionAndBackup() {
        int foundVersion = configYaml.getInt("config-version", -1);
        if (foundVersion < CURRENT_CONFIG_VERSION) {
            // Backup old file
            File backupFile = new File(configFile.getParent(), "config-backup-" + getTimestamp() + ".yml");
            try {
                Files.copy(configFile.toPath(), backupFile.toPath());
                plugin.getLogger().info("Backed up old config to: " + backupFile.getName());
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to backup old config: " + e.getMessage());
            }

            // Overwrite config
            save();
        }
    }

    private static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }

    public static HomeConfig getHomeConfig() {
        return config.homes;
    }

    public static AuctionConfig getAuctionConfig() {
        return config.auction;
    }

    public static ModConfigWrapper getConfig() {
        return config;
    }
}
