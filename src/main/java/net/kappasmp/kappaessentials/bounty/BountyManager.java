package net.kappasmp.kappaessentials.bounty;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class BountyManager {

    private static final Map<UUID, BountyData> bounties = new HashMap<>();
    private static File bountyFile;
    private static final Yaml YAML;

    static {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        YAML = new Yaml(options);
    }

    public static void initialize(JavaPlugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        bountyFile = new File(dataFolder, "bounties.yml");
        loadBounties();
    }

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
        if (bountyFile == null) return;

        Map<String, Map<String, Object>> yamlData = new HashMap<>();
        for (Map.Entry<UUID, BountyData> entry : bounties.entrySet()) {
            Map<String, Object> bountyDetails = new HashMap<>();
            bountyDetails.put("amount", entry.getValue().amount());
            bountyDetails.put("setter", entry.getValue().setter().toString());
            yamlData.put(entry.getKey().toString(), bountyDetails);
        }

        try (Writer writer = new FileWriter(bountyFile)) {
            YAML.dump(yamlData, writer);
        } catch (IOException e) {
            System.err.println("Failed to save bounties: " + e.getMessage());
        }
    }

    public static void loadBounties() {
        if (bountyFile == null || !bountyFile.exists()) return;

        try (InputStream input = new FileInputStream(bountyFile)) {
            Map<String, Map<String, Object>> yamlData = YAML.load(input);
            if (yamlData == null) return;

            bounties.clear();
            for (Map.Entry<String, Map<String, Object>> entry : yamlData.entrySet()) {
                UUID target = UUID.fromString(entry.getKey());
                Map<String, Object> details = entry.getValue();
                int amount = (int) details.get("amount");
                UUID setter = UUID.fromString(details.get("setter").toString());
                bounties.put(target, new BountyData(amount, setter));
            }
        } catch (IOException e) {
            System.err.println("Failed to load bounties: " + e.getMessage());
        }
    }

    public record BountyData(int amount, UUID setter) {}
}
