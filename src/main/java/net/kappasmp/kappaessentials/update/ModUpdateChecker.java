package net.kappasmp.kappaessentials.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModUpdateChecker {

    private static final String PROJECT_ID = "kappaessentials";
    private static final String API_URL = "https://api.modrinth.com/v2/project/" + PROJECT_ID + "/version";

    private static String latestVersion = null;

    // Call this on plugin startup
    public static void checkForUpdates(String currentVersion) {
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("KappaEssentials"), () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
                connection.setRequestProperty("User-Agent", "KappaEssentials");

                JsonArray versions = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonArray();
                if (!versions.isEmpty()) {
                    JsonObject latest = versions.get(0).getAsJsonObject();
                    latestVersion = latest.get("version_number").getAsString();
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[KappaEssentials] Failed to check for updates: " + e.getMessage());
            }
        });
    }

    // Call this when a player joins (or runs /version or similar)
    public static void notifyIfOutdated(Player player, String currentVersion) {
        if (latestVersion != null && !latestVersion.equalsIgnoreCase(currentVersion)) {
            player.sendMessage(ChatColor.YELLOW + "A new version of " + ChatColor.GOLD + "KappaEssentials" +
                    ChatColor.YELLOW + " is available: " + ChatColor.GREEN + latestVersion +
                    ChatColor.GRAY + " (current: " + currentVersion + ").");
            player.sendMessage(ChatColor.AQUA + "Download at: https://modrinth.com/mod/kappaessentials");
        }
    }
}
