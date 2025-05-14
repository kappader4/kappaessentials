package net.kappasmp.kappaessentials.token;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class TokenManager {
    private static final Map<UUID, Integer> tokenMap = new HashMap<>();
    private static final Gson gson = new Gson();
    private static final File directory = new File("config/KappaEssentials");
    private static final File file = new File(directory, "tokens.json");

    public static int getTokens(UUID uuid) {
        return tokenMap.getOrDefault(uuid, 0);
    }

    public static void addTokens(UUID uuid, int amount) {
        tokenMap.put(uuid, getTokens(uuid) + amount);
        save(); // Save after update
    }

    public static void giveTokens(UUID uuid, int amount) {
        addTokens(uuid, amount);
    }

    public static boolean withdrawTokens(UUID uuid, int amount) {
        int current = getTokens(uuid);
        if (current >= amount) {
            tokenMap.put(uuid, current - amount);
            save(); // Save after withdrawal
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

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> rawMap = gson.fromJson(reader, type);

            tokenMap.clear();
            for (Map.Entry<String, Integer> entry : rawMap.entrySet()) {
                tokenMap.put(UUID.fromString(entry.getKey()), entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Map<String, Integer> rawMap = new HashMap<>();
        for (Map.Entry<UUID, Integer> entry : tokenMap.entrySet()) {
            rawMap.put(entry.getKey().toString(), entry.getValue());
        }

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(rawMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
