package net.kappasmp.kappaessentials.token;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TokenManager {
    private static final Map<UUID, Integer> tokenMap = new HashMap<>();
    private static final Yaml YAML;
    private static File file;

    static {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        YAML = new Yaml(options);
    }

    public static void init(File pluginDataFolder) {
        if (!pluginDataFolder.exists()) {
            pluginDataFolder.mkdirs();
        }

        file = new File(pluginDataFolder, "tokens.yml");
        load();
    }

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
        if (file == null) {
            throw new IllegalStateException("TokenManager file not initialized. Call TokenManager.init(plugin.getDataFolder()) first.");
        }

        if (!file.exists()) return;

        try (InputStream input = new FileInputStream(file)) {
            Map<String, Object> raw = YAML.load(input);
            if (raw == null) return;

            tokenMap.clear();
            for (Map.Entry<String, Object> entry : raw.entrySet()) {
                try {
                    UUID uuid = UUID.fromString(entry.getKey());
                    int tokens = Integer.parseInt(entry.getValue().toString());
                    tokenMap.put(uuid, tokens);
                } catch (Exception e) {
                    e.printStackTrace(); // log bad entries
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        if (file == null) {
            throw new IllegalStateException("TokenManager file not initialized. Call TokenManager.init(plugin.getDataFolder()) first.");
        }

        Map<String, Object> raw = new HashMap<>();
        for (Map.Entry<UUID, Integer> entry : tokenMap.entrySet()) {
            raw.put(entry.getKey().toString(), entry.getValue());
        }

        try (Writer writer = new FileWriter(file)) {
            YAML.dump(raw, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
