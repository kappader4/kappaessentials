package net.kappasmp.kappaessentials.homes;

import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.config.HomeConfig;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {

    public static class HomeData {
        public Location location;

        public HomeData(Location location) {
            this.location = location;
        }
    }

    private static final File FILE = new File("plugins/KappaEssentials/homes.yml");
    private static final Map<UUID, Map<String, HomeData>> homes = new HashMap<>();
    private static final Map<UUID, Long> lastTeleportTime = new HashMap<>();

    public static boolean setHome(Player player, String name, HomeData data) {
        UUID uuid = player.getUniqueId();
        Map<String, HomeData> playerHomes = homes.computeIfAbsent(uuid, k -> new HashMap<>());
        HomeConfig config = ConfigManager.getHomeConfig();

        // Cooldown check
        if (config.cooldownSeconds > 0 && lastTeleportTime.containsKey(uuid)) {
            long elapsed = (System.currentTimeMillis() - lastTeleportTime.get(uuid)) / 1000;
            if (elapsed < config.cooldownSeconds) return false;
        }

        // Cross-dimension check
        if (!config.crossDimensionAllowed && !player.getWorld().equals(data.location.getWorld())) {
            return false;
        }

        // Max homes check
        int maxHomes = getMaxHomeLimit(player, config);
        boolean isNew = !playerHomes.containsKey(name);
        if (isNew && playerHomes.size() >= maxHomes) return false;

        // Save home
        playerHomes.put(name, data);
        saveHomes();
        lastTeleportTime.put(uuid, System.currentTimeMillis());
        return true;
    }

    public static boolean removeHome(Player player, String name) {
        UUID uuid = player.getUniqueId();
        Map<String, HomeData> playerHomes = homes.get(uuid);
        if (playerHomes != null && playerHomes.remove(name) != null) {
            saveHomes();
            return true;
        }
        return false;
    }

    public static HomeData getHome(Player player, String name) {
        return homes.getOrDefault(player.getUniqueId(), Collections.emptyMap()).get(name);
    }

    public static Map<String, HomeData> getHomes(Player player) {
        return homes.getOrDefault(player.getUniqueId(), Collections.emptyMap());
    }

    public static void loadHomes() {
        if (!FILE.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(FILE);
        homes.clear();

        for (String uuidStr : yaml.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            ConfigurationSection playerSection = yaml.getConfigurationSection(uuidStr);
            if (playerSection == null) continue;

            Map<String, HomeData> playerHomes = new HashMap<>();
            for (String homeName : playerSection.getKeys(false)) {
                ConfigurationSection homeSection = playerSection.getConfigurationSection(homeName);
                if (homeSection == null) continue;

                String worldName = homeSection.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;

                double x = homeSection.getDouble("x");
                double y = homeSection.getDouble("y");
                double z = homeSection.getDouble("z");
                float yaw = (float) homeSection.getDouble("yaw");
                float pitch = (float) homeSection.getDouble("pitch");

                Location loc = new Location(world, x, y, z, yaw, pitch);
                playerHomes.put(homeName, new HomeData(loc));
            }
            homes.put(uuid, playerHomes);
        }
    }

    public static void saveHomes() {
        YamlConfiguration yaml = new YamlConfiguration();

        for (Map.Entry<UUID, Map<String, HomeData>> entry : homes.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<String, HomeData> homeEntry : entry.getValue().entrySet()) {
                Location loc = homeEntry.getValue().location;
                String path = uuidStr + "." + homeEntry.getKey();

                yaml.set(path + ".world", loc.getWorld().getName());
                yaml.set(path + ".x", loc.getX());
                yaml.set(path + ".y", loc.getY());
                yaml.set(path + ".z", loc.getZ());
                yaml.set(path + ".yaw", loc.getYaw());
                yaml.set(path + ".pitch", loc.getPitch());
            }
        }

        try {
            yaml.save(FILE);
        } catch (IOException e) {
            Bukkit.getLogger().warning("[KappaEssentials] Failed to save homes.yml: " + e.getMessage());
        }
    }

    private static int getMaxHomeLimit(Player player, HomeConfig config) {
        int max = config.defaultLimit;

        for (Map.Entry<String, Integer> entry : config.rankLimits.entrySet()) {
            if (hasLuckPermsPermission(player, entry.getKey())) {
                max = Math.max(max, entry.getValue());
            }
        }

        return max;
    }

    private static boolean hasLuckPermsPermission(Player player, String permission) {
        try {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(player.getUniqueId());
            if (user == null) return false;

            ContextManager contextManager = api.getContextManager();
            QueryOptions options = contextManager.getQueryOptions(user).orElse(null);
            if (options == null) return false;

            return user.getCachedData().getPermissionData(options).checkPermission(permission).asBoolean();
        } catch (Exception e) {
            return false;
        }
    }
}
