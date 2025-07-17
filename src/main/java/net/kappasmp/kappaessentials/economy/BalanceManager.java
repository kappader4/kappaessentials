package net.kappasmp.kappaessentials.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BalanceManager {

    private static final Map<UUID, Integer> playerBalances = new HashMap<>();
    private static final Map<String, Integer> itemPrices = new HashMap<>();

    private static File balanceFile;
    private static File priceFile;
    private static FileConfiguration balanceConfig;
    private static FileConfiguration priceConfig;

    private static Plugin plugin;

    public static void init(Plugin owningPlugin, java.nio.file.Path configDir) {
        plugin = owningPlugin;
        File dataDir = configDir.resolve("KappaEssentials").toFile();

        if (!dataDir.exists()) dataDir.mkdirs();

        balanceFile = new File(dataDir, "balance.yml");
        priceFile = new File(dataDir, "prices.yml");

        balanceConfig = YamlConfiguration.loadConfiguration(balanceFile);
        priceConfig = YamlConfiguration.loadConfiguration(priceFile);

        loadBalances();
        loadItemPrices();
    }

    public static void loadBalances() {
        playerBalances.clear();
        for (String key : balanceConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int balance = balanceConfig.getInt(key);
                playerBalances.put(uuid, balance);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public static void loadItemPrices() {
        itemPrices.clear();
        if (priceConfig.getKeys(false).isEmpty()) {
            initializeDefaultItemPrices();
            saveItemPrices();
        } else {
            for (String key : priceConfig.getKeys(false)) {
                int price = priceConfig.getInt(key);
                itemPrices.put(key, price);
            }
        }
    }

    public static void saveBalances() {
        for (Map.Entry<UUID, Integer> entry : playerBalances.entrySet()) {
            balanceConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            balanceConfig.save(balanceFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save balance.yml: " + e.getMessage());
        }
    }

    public static void saveItemPrices() {
        for (Map.Entry<String, Integer> entry : itemPrices.entrySet()) {
            priceConfig.set(entry.getKey(), entry.getValue());
        }
        try {
            priceConfig.save(priceFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save prices.yml: " + e.getMessage());
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

    public static boolean withdrawBalance(UUID uuid, int amount) {
        int balance = getBalance(uuid);
        if (balance >= amount) {
            setBalance(uuid, balance - amount);
            return true;
        }
        return false;
    }

    public static int getItemPrice(String itemId) {
        return itemPrices.getOrDefault(itemId, 0);
    }

    public static void setItemPrice(String itemId, int price) {
        itemPrices.put(itemId, price);
        saveItemPrices();
    }

    public static void reloadBalances() {
        balanceConfig = YamlConfiguration.loadConfiguration(balanceFile);
        loadBalances();
    }

    public static void reloadPrices() {
        priceConfig = YamlConfiguration.loadConfiguration(priceFile);
        loadItemPrices();
    }

    public static List<Map.Entry<UUID, Integer>> getTopBalances(int limit) {
        return playerBalances.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public static void savePlayerBalance(Player player) {
        saveBalances();
    }

    private static void initializeDefaultItemPrices() {
        setItemPrice("minecraft:stone", 1);
        setItemPrice("minecraft:diamond", 15);
        setItemPrice("minecraft:iron_ore", 5);
    }
}
