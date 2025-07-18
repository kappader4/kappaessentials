package net.kappasmp.kappaessentials.bounty;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BountyManager {

    private static final Map<UUID, BountyData> bounties = new HashMap<>();
    private static File bountyFile;
    public static YamlConfiguration bountyConfig;

    public static void initialize(Plugin plugin) {
        File dataFolder = new File(plugin.getDataFolder(), "bounties");
        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs();
            if (!created) {
                plugin.getLogger().warning("Failed to create bounties folder.");
            }
        }

        bountyFile = new File(dataFolder, "bounties.yml");
        if (!bountyFile.exists()) {
            try {
                boolean created = bountyFile.createNewFile();
                if (!created) {
                    plugin.getLogger().warning("Could not create bounties.yml.");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create bounty file: " + e.getMessage());
            }
        }

        bountyConfig = YamlConfiguration.loadConfiguration(bountyFile);
        loadBounties();
    }

    public static void setBounty(UUID target, UUID setter, int amount) {
        bounties.put(target, new BountyData(amount, setter));
        saveBounties();
    }

    public static int getBounty(UUID target) {
        return bounties.getOrDefault(target, new BountyData(0, null)).amount();
    }

    public static UUID getSetter(UUID target) {
        BountyData data = bounties.get(target);
        return data != null ? data.setter() : null;
    }

    public static boolean hasBounty(UUID target) {
        return bounties.containsKey(target);
    }

    public static Set<UUID> getAllBountiedPlayers() {
        return Collections.unmodifiableSet(bounties.keySet());
    }

    public static void removeBounty(UUID target) {
        bounties.remove(target);
        bountyConfig.set(target.toString(), null);
        saveBounties();
    }

    public static void saveBounties() {
        if (bountyConfig == null || bountyFile == null) {
            Bukkit.getLogger().severe("[KappaEssentials] Cannot save bounties: config not initialized.");
            return;
        }

        for (Map.Entry<UUID, BountyData> entry : bounties.entrySet()) {
            String uuid = entry.getKey().toString();
            BountyData data = entry.getValue();
            bountyConfig.set(uuid + ".amount", data.amount());
            bountyConfig.set(uuid + ".setter", data.setter() != null ? data.setter().toString() : null);
        }

        try {
            bountyConfig.save(bountyFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save bounties.yml: " + e.getMessage());
        }
    }

    public static void loadBounties() {
        bounties.clear();

        if (bountyConfig == null) {
            Bukkit.getLogger().severe("[KappaEssentials] bountyConfig is null! Skipping bounty loading.");
            return;
        }

        for (String key : bountyConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int amount = bountyConfig.getInt(key + ".amount");
                String setterStr = bountyConfig.getString(key + ".setter");
                UUID setter = (setterStr != null && !setterStr.isEmpty()) ? UUID.fromString(setterStr) : null;
                bounties.put(uuid, new BountyData(amount, setter));
            } catch (Exception e) {
                Bukkit.getLogger().warning("[KappaEssentials] Failed to load bounty for key: " + key + " (" + e.getMessage() + ")");
            }
        }
    }

    public record BountyData(int amount, UUID setter) {}
}
