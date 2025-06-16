package net.kappasmp.kappaessentials.homes;

import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.config.HomeConfig;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {

    public static class HomeData {
        private final double x, y, z;
        private final String world;

        public HomeData(double x, double y, double z, String world) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
        }

        public Location toLocation() {
            World w = Bukkit.getWorld(world);
            return w != null ? new Location(w, x, y, z) : null;
        }

        public String getWorld() {
            return world;
        }

        public int getX() {
            return (int) x;
        }

        public int getY() {
            return (int) y;
        }

        public int getZ() {
            return (int) z;
        }
    }

    private static File file;
    private static YamlConfiguration config;
    private static final Map<UUID, Map<String, HomeData>> homes = new HashMap<>();
    private static final Map<UUID, Long> lastTeleportTime = new HashMap<>();

    public static void init(File pluginFolder) {
        File dataFolder = new File(pluginFolder, "KappaEssentials");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        file = new File(dataFolder, "homes.yml");
        config = YamlConfiguration.loadConfiguration(file);
        loadHomes();
    }

    public static void loadHomes() {
        if (!file.exists()) return;

        homes.clear();
        for (String uuidStr : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                ConfigurationSection userSection = config.getConfigurationSection(uuidStr);
                if (userSection == null) continue;

                Map<String, HomeData> playerHomes = new HashMap<>();
                for (String homeName : userSection.getKeys(false)) {
                    ConfigurationSection homeSec = userSection.getConfigurationSection(homeName);
                    if (homeSec == null) continue;

                    double x = homeSec.getDouble("x");
                    double y = homeSec.getDouble("y");
                    double z = homeSec.getDouble("z");
                    String world = homeSec.getString("world", "world");

                    playerHomes.put(homeName, new HomeData(x, y, z, world));
                }

                homes.put(uuid, playerHomes);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public static void saveHomes() {
        config = new YamlConfiguration();

        for (Map.Entry<UUID, Map<String, HomeData>> entry : homes.entrySet()) {
            String uuidStr = entry.getKey().toString();
            Map<String, HomeData> playerHomes = entry.getValue();

            for (Map.Entry<String, HomeData> homeEntry : playerHomes.entrySet()) {
                String homeName = homeEntry.getKey();
                HomeData home = homeEntry.getValue();

                String path = uuidStr + "." + homeName;
                config.set(path + ".x", home.getX());
                config.set(path + ".y", home.getY());
                config.set(path + ".z", home.getZ());
                config.set(path + ".world", home.getWorld());
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            System.err.println("Failed to save homes.yml: " + e.getMessage());
        }
    }

    public static void reloadHomes() {
        config = YamlConfiguration.loadConfiguration(file);
        loadHomes();
    }

    public static void setHome(Player player, String name, HomeData data) {
        UUID uuid = player.getUniqueId();
        Map<String, HomeData> playerHomes = homes.computeIfAbsent(uuid, k -> new HashMap<>());

        HomeConfig config = ConfigManager.getHomeConfig();

        if (config.cooldownSeconds > 0 && lastTeleportTime.containsKey(uuid)) {
            long elapsed = (System.currentTimeMillis() - lastTeleportTime.get(uuid)) / 1000;
            if (elapsed < config.cooldownSeconds) {
                long remaining = config.cooldownSeconds - elapsed;
                player.sendMessage("§cYou must wait " + remaining + "s before setting another home.");
                return;
            }
        }

        if (!config.crossDimensionAllowed && !player.getWorld().getName().equalsIgnoreCase(data.getWorld())) {
            player.sendMessage("§cYou are not allowed to set homes across dimensions.");
            return;
        }

        int maxHomes = config.defaultLimit;
        for (Map.Entry<String, Integer> entry : config.rankLimits.entrySet()) {
            if (hasLuckPermsPermission(player, entry.getKey())) {
                maxHomes = Math.max(maxHomes, entry.getValue());
            }
        }

        boolean isNew = !playerHomes.containsKey(name);
        if (isNew && playerHomes.size() >= maxHomes) {
            player.sendMessage("§cYou have reached your home limit of §e" + maxHomes + "§c.");
            return;
        }

        playerHomes.put(name, data);
        saveHomes();
        lastTeleportTime.put(uuid, System.currentTimeMillis());

        if (config.showCountdownMessages) {
            player.sendMessage(isNew
                    ? "§aHome '" + name + "' has been set."
                    : "§cHome '" + name + "' already exists (please cure your dementia).");
        }
    }

    public static HomeData getHome(Player player, String homeName) {
        return homes.getOrDefault(player.getUniqueId(), Collections.emptyMap()).get(homeName);
    }

    public static Map<String, HomeData> getHomes(Player player) {
        return homes.getOrDefault(player.getUniqueId(), Collections.emptyMap());
    }

    public static void removeHome(Player player, String homeName) {
        UUID uuid = player.getUniqueId();
        Map<String, HomeData> playerHomes = homes.get(uuid);
        if (playerHomes != null && playerHomes.containsKey(homeName)) {
            playerHomes.remove(homeName);
            saveHomes();
        }
    }

    private static boolean hasLuckPermsPermission(Player player, String permission) {
        try {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(player.getUniqueId());
            if (user == null) return false;

            ContextManager contextManager = api.getContextManager();
            QueryOptions queryOptions = contextManager.getQueryOptions(user).orElse(null);
            if (queryOptions == null) return false;

            return user.getCachedData().getPermissionData(queryOptions).checkPermission(permission).asBoolean();
        } catch (IllegalStateException e) {
            return false;
        }
    }
}
