package net.kappasmp.kappaessentials.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModUpdateChecker {
    private static final String PROJECT_ID = "kappaessentials"; // e.g. "kappaessentials"
    private static final String API_URL = "https://api.modrinth.com/v2/project/" + PROJECT_ID + "/version";

    private static String latestVersion = null;

    public static void checkForUpdates(String currentVersion) {
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
                connection.setRequestProperty("User-Agent", "KappaEssentials");

                JsonArray versions = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonArray();
                if (!versions.isEmpty()) {
                    JsonObject latest = versions.get(0).getAsJsonObject();
                    latestVersion = latest.get("version_number").getAsString();
                }
            } catch (Exception e) {
                System.err.println("[KappaEssentials] Failed to check for updates: " + e.getMessage());
            }
        }).start();
    }

    public static void notifyIfOutdated(ServerPlayerEntity player, String currentVersion) {
        if (latestVersion != null && !latestVersion.equalsIgnoreCase(currentVersion)) {
            player.sendMessage(Text.literal("§eA new version of §6KappaEssentials §eis available: §a" + latestVersion +
                    " §7(current: " + currentVersion + "). Download it at §bhttps://modrinth.com/mod/kappaessentials"), false);
        }
    }
}
