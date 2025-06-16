package net.kappasmp.kappaessentials.economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

    public static void init(JavaPlugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        balanceFile = new File(dataFolder, "balances.yml");
        priceFile = new File(dataFolder, "prices.yml");

        // Create files if they don't exist
        try {
            if (!balanceFile.exists()) balanceFile.createNewFile();
            if (!priceFile.exists()) priceFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid UUID in balances.yml: " + key);
            }
        }
    }

    public static void loadItemPrices() {
        itemPrices.clear();
        for (String key : priceConfig.getKeys(false)) {
            itemPrices.put(key, priceConfig.getInt(key));
        }
        // If empty, initialize defaults
        if (itemPrices.isEmpty()) {
            initializeDefaultItemPrices();
            saveItemPrices();
        }
    }

    public static void saveBalances() {
        for (Map.Entry<UUID, Integer> entry : playerBalances.entrySet()) {
            balanceConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            balanceConfig.save(balanceFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePlayerBalance(Player player) {
        UUID uuid = player.getUniqueId();
        balanceConfig.set(uuid.toString(), getBalance(uuid));
        playerBalances.put(uuid, getBalance(uuid));
        try {
            balanceConfig.save(balanceFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveItemPrices() {
        for (Map.Entry<String, Integer> entry : itemPrices.entrySet()) {
            priceConfig.set(entry.getKey(), entry.getValue());
        }
        try {
            priceConfig.save(priceFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getBalance(UUID uuid) {
        return playerBalances.getOrDefault(uuid, 0);
    }

    public static void setBalance(UUID uuid, int amount) {
        playerBalances.put(uuid, amount);
        balanceConfig.set(uuid.toString(), amount);
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
        priceConfig.set(itemId, price);
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

    private static void initializeDefaultItemPrices() {
        itemPrices.put("minecraft:stone", 1);
        itemPrices.put("minecraft:diamond", 15);
        itemPrices.put("minecraft:iron_ore", 5);
    }

    public static boolean withdrawBalance(UUID uuid, int amount) {
        int current = getBalance(uuid);
        if (current >= amount) {
            setBalance(uuid, current - amount);
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

    public static void reload(JavaPlugin plugin) {
        balanceConfig = YamlConfiguration.loadConfiguration(balanceFile);
        priceConfig = YamlConfiguration.loadConfiguration(priceFile);
        loadBalances();
        loadItemPrices();
    }
}
