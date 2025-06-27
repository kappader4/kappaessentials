package net.kappasmp.kappaessentials.bounty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class BountyManager {

    public static final Map<UUID, BountyData> bounties = new HashMap<>();
    private static final File BOUNTY_FILE = new File("config/KappaEssentials/bounties.json");

    public static void setBounty(UUID target, UUID setter, int amount) {
        bounties.put(target, new BountyData(amount, setter));
        saveBounties();
    }

    public static int getBounty(UUID target) {
        BountyData data = bounties.get(target);
        return data != null ? data.amount() : 0;
    }

    public static UUID getSetter(UUID target) {
        BountyData data = bounties.get(target);
        return data != null ? data.setter() : null;
    }

    public static boolean hasBounty(UUID target) {
        return bounties.containsKey(target);
    }

    public static Set<UUID> getAllBountiedPlayers() {
        return bounties.keySet();
    }

    public static void removeBounty(UUID target) {
        bounties.remove(target);
        saveBounties();
    }

    public static void saveBounties() {
        try (FileWriter writer = new FileWriter(BOUNTY_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<String, BountyData> stringMap = new HashMap<>();
            for (Map.Entry<UUID, BountyData> entry : bounties.entrySet()) {
                stringMap.put(entry.getKey().toString(), entry.getValue());
            }
            gson.toJson(stringMap, writer);
        } catch (IOException e) {
            System.err.println("Failed to save bounties: " + e.getMessage());
        }
    }

    public static void loadBounties() {
        if (!BOUNTY_FILE.exists()) return;

        try (FileReader reader = new FileReader(BOUNTY_FILE)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, BountyData>>() {}.getType();
            Map<String, BountyData> stringMap = gson.fromJson(reader, type);
            bounties.clear();
            for (Map.Entry<String, BountyData> entry : stringMap.entrySet()) {
                bounties.put(UUID.fromString(entry.getKey()), entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Failed to load bounties: " + e.getMessage());
        }
    }

    // Record to store both amount and setter
    public record BountyData(int amount, UUID setter) {}
}
