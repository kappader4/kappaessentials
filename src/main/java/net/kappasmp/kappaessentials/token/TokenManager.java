package net.kappasmp.kappaessentials.token;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TokenManager {
    private static final Map<UUID, Integer> tokenMap = new HashMap<>();
    private static final File folder = new File("plugins/kappaessentials");
    private static final File file = new File(folder, "tokens.yml");

    public static int getTokens(UUID uuid) {
        return tokenMap.getOrDefault(uuid, 0);
    }

    public static void addTokens(UUID uuid, int amount) {
        tokenMap.put(uuid, getTokens(uuid) + amount);
        save();
    }

    public static void giveTokens(UUID uuid, int amount) {
        addTokens(uuid, amount);
    }

    public static boolean withdrawTokens(UUID uuid, int amount) {
        int current = getTokens(uuid);
        if (current >= amount) {
            tokenMap.put(uuid, current - amount);
            save();
            return true;
        }
        return false;
    }

    public static List<Map.Entry<UUID, Integer>> getTopTokens(int amount) {
        return tokenMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(amount)
                .collect(Collectors.toList());
    }

    public static void load() {
        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        tokenMap.clear();

        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int tokens = config.getInt(key);
                tokenMap.put(uuid, tokens);
            } catch (IllegalArgumentException ignored) {
                System.err.println("[KappaEssentials] Invalid UUID in tokens.yml: " + key);
            }
        }
    }

    public static void save() {
        if (!folder.exists()) folder.mkdirs();

        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : tokenMap.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            System.err.println("[KappaEssentials] Failed to save tokens.yml: " + e.getMessage());
        }
    }
}
