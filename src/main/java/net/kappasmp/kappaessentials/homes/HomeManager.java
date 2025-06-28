package net.kappasmp.kappaessentials.homes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.config.HomeConfig;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class HomeManager {

    public static class HomeData {
        public BlockPos pos;
        public RegistryKey<World> dimension;

        public HomeData(BlockPos pos, RegistryKey<World> dimension) {
            this.pos = pos;
            this.dimension = dimension;
        }
    }

    private static final File FILE = new File("config/KappaEssentials/homes.json");
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    private static final Map<UUID, Map<String, HomeData>> homes = new HashMap<>();
    private static final Map<UUID, Long> lastTeleportTime = new HashMap<>();

    public static void setHome(ServerPlayerEntity player, String name, HomeData data) {
        UUID uuid = player.getUuid();
        Map<String, HomeData> playerHomes = homes.computeIfAbsent(uuid, k -> new HashMap<>());

        HomeConfig config = ConfigManager.getHomeConfig();

        // Cooldown check
        if (config.cooldownSeconds > 0 && lastTeleportTime.containsKey(uuid)) {
            long elapsed = (System.currentTimeMillis() - lastTeleportTime.get(uuid)) / 1000;
            if (elapsed < config.cooldownSeconds) {
                long remaining = config.cooldownSeconds - elapsed;
                player.sendMessage(Text.literal("§cYou must wait " + remaining + "s before setting another home."), false);
                return;
            }
        }

        // 1. Check cross-dimension teleport allowance
        if (!config.crossDimensionAllowed && !player.getWorld().getRegistryKey().equals(data.dimension)) {
            player.sendMessage(Text.literal("§cYou are not allowed to set homes across dimensions."), false);
            return;
        }

        // 2. Calculate max homes allowed
        int maxHomes = config.defaultLimit;
        for (Map.Entry<String, Integer> entry : config.rankLimits.entrySet()) {
            if (hasLuckPermsPermission(player, entry.getKey())) {
                maxHomes = Math.max(maxHomes, entry.getValue());
            }
        }

        // 3. Enforce home limit
        boolean isNew = !playerHomes.containsKey(name);
        if (isNew && playerHomes.size() >= maxHomes) {
            player.sendMessage(Text.literal("§cYou have reached your home limit of §e" + maxHomes + "§c."), false);
            return;
        }

        // 4. Save and confirm once
        playerHomes.put(name, data);
        saveHomes();
        lastTeleportTime.put(uuid, System.currentTimeMillis());

        if (config.showCountdownMessages) {
            if (isNew) {
                player.sendMessage(Text.literal("§aHome '" + name + "' has been set."), false);
            } else {
                player.sendMessage(Text.literal("§cHome '" + name + "' already exists (please cure your dementia)."), false);
            }
        }
    }

    public static HomeData getHome(ServerPlayerEntity player, String homeName) {
        return homes.getOrDefault(player.getUuid(), Collections.emptyMap()).get(homeName);
    }

    public static Map<String, HomeData> getHomes(ServerPlayerEntity player) {
        return homes.getOrDefault(player.getUuid(), Collections.emptyMap());
    }

    public static void removeHome(ServerPlayerEntity player, String homeName) {
        UUID uuid = player.getUuid();
        Map<String, HomeData> playerHomes = homes.get(uuid);
        if (playerHomes != null) {
            playerHomes.remove(homeName);
            saveHomes();
        }
    }

    public static void loadHomes() {
        if (!FILE.exists()) return;

        try (FileReader reader = new FileReader(FILE)) {
            Type type = new TypeToken<Map<String, JsonObject>>() {}.getType();
            Map<String, JsonObject> loaded = GSON.fromJson(reader, type);

            if (loaded != null) {
                homes.clear();
                for (Map.Entry<String, JsonObject> entry : loaded.entrySet()) {
                    UUID uuid = UUID.fromString(entry.getKey());
                    JsonObject homesJson = entry.getValue();
                    Map<String, HomeData> playerHomes = new HashMap<>();

                    for (Map.Entry<String, JsonElement> homeEntry : homesJson.entrySet()) {
                        String homeName = homeEntry.getKey();
                        JsonObject posJson = homeEntry.getValue().getAsJsonObject();

                        int x = posJson.get("x").getAsInt();
                        int y = posJson.get("y").getAsInt();
                        int z = posJson.get("z").getAsInt();
                        String dimId = posJson.has("dimension") ? posJson.get("dimension").getAsString() : "minecraft:overworld";

                        Identifier id = Identifier.tryParse(dimId);
                        RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD, id);
                        playerHomes.put(homeName, new HomeData(new BlockPos(x, y, z), dimension));
                    }

                    homes.put(uuid, playerHomes);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load homes.json: " + e.getMessage());
        }
    }

    private static void saveHomes() {
        try {
            FILE.getParentFile().mkdirs();
            Map<String, JsonObject> homesToSave = new HashMap<>();

            for (Map.Entry<UUID, Map<String, HomeData>> entry : homes.entrySet()) {
                JsonObject homesJson = new JsonObject();
                for (Map.Entry<String, HomeData> homeEntry : entry.getValue().entrySet()) {
                    HomeData home = homeEntry.getValue();
                    JsonObject json = new JsonObject();
                    json.addProperty("x", home.pos.getX());
                    json.addProperty("y", home.pos.getY());
                    json.addProperty("z", home.pos.getZ());
                    json.addProperty("dimension", home.dimension.getValue().toString());
                    homesJson.add(homeEntry.getKey(), json);
                }
                homesToSave.put(entry.getKey().toString(), homesJson);
            }

            try (FileWriter writer = new FileWriter(FILE)) {
                GSON.toJson(homesToSave, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to save homes.json: " + e.getMessage());
        }
    }

    private static boolean hasLuckPermsPermission(ServerPlayerEntity player, String permission) {
        try {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(player.getUuid());
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