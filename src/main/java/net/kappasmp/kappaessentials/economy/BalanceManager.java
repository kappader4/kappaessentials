package net.kappasmp.kappaessentials.economy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class BalanceManager {

    private static final Map<UUID, Integer> playerBalances = new HashMap<>();
    private static final Map<String, Integer> itemPrices = new HashMap<>();

    private static File balanceFile;
    private static File priceFile;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create(); // ← This enables dropdown formatting

    public static void init(MinecraftServer server) {
        Path dataDir = server.getRunDirectory().resolve("config/KappaEssentials");
        balanceFile = dataDir.resolve("balance.json").toFile();
        priceFile = dataDir.resolve("prices.json").toFile();

        if (!dataDir.toFile().exists()) {
            dataDir.toFile().mkdirs();
        }

        loadBalances();
        loadItemPrices();
    }

    public static void loadBalances() {
        if (balanceFile.exists()) {
            try (FileReader reader = new FileReader(balanceFile)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                playerBalances.clear();
                for (Map.Entry<String, com.google.gson.JsonElement> entry : json.entrySet()) {
                    playerBalances.put(UUID.fromString(entry.getKey()), entry.getValue().getAsInt());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadItemPrices() {
        if (priceFile.exists()) {
            try (FileReader reader = new FileReader(priceFile)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                itemPrices.clear();
                for (Map.Entry<String, com.google.gson.JsonElement> entry : json.entrySet()) {
                    itemPrices.put(entry.getKey(), entry.getValue().getAsInt());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            initializeDefaultItemPrices();
        }
    }

    public static void saveBalances() {
        try (FileWriter writer = new FileWriter(balanceFile)) {
            JsonObject json = new JsonObject();
            playerBalances.forEach((uuid, balance) -> json.addProperty(uuid.toString(), balance));
            gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save only a single player's balance (for logout events)
    public static void savePlayerBalance(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        int balance = getBalance(uuid);

        try (FileReader reader = new FileReader(balanceFile)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            json.addProperty(uuid.toString(), balance);

            try (FileWriter writer = new FileWriter(balanceFile)) {
                gson.toJson(json, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveItemPrices() {
        try (FileWriter writer = new FileWriter(priceFile)) {
            JsonObject json = new JsonObject();
            itemPrices.forEach(json::addProperty);
            gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getBalance(UUID uuid) {
        return playerBalances.getOrDefault(uuid, 0);
    }

    public static void setBalance(UUID uuid, int amount) {
        playerBalances.put(uuid, amount);
        saveBalances();
    }

    public static void addBalance(UUID uuid, int amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public static void subtractBalance(UUID uuid, int amount) {
        setBalance(uuid, getBalance(uuid) - amount);
    }

    public static int getItemPrice(String itemId) {
        return itemPrices.getOrDefault(itemId, 0);
    }

    public static void setItemPrice(String itemId, int price) {
        itemPrices.put(itemId, price);
        saveItemPrices();
    }

    public static void reloadBalances() {
        loadBalances();
    }

    public static void reloadPrices() {
        loadItemPrices();
    }

    private static void initializeDefaultItemPrices() {
        setItemPrice("minecraft:stone", 1);
        setItemPrice("minecraft:diamond", 15);
        setItemPrice("minecraft:iron_ore", 5);
    }

    public static boolean withdrawBalance(UUID playerUUID, int amount) {
        int balance = getBalance(playerUUID);
        if (balance >= amount) {
            setBalance(playerUUID, balance - amount);
            return true;
        }
        return false;
    }

    public static List<Map.Entry<UUID, Integer>> getTopBalances(int limit) {
        return playerBalances.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
