package net.kappasmp.kappaessentials.command;

import net.kappasmp.kappaessentials.token.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AfkCommand implements CommandExecutor {

    private final Plugin plugin;
    private final Map<UUID, Location> afkStartLocations = new HashMap<>();
    private final Map<UUID, Integer> afkTasks = new HashMap<>();

    public AfkCommand(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        UUID uuid = player.getUniqueId();

        if (args.length > 0 && args[0].equalsIgnoreCase("stop")) {
            if (afkTasks.containsKey(uuid)) {
                cancelAfk(uuid);
                player.sendMessage("§7You are no longer AFK.");
            } else {
                player.sendMessage("§cYou're not AFK right now.");
            }
            return true;
        }

        if (afkTasks.containsKey(uuid)) {
            player.sendMessage("§cYou're already AFK! Use §e/afk stop §cto cancel it.");
            return true;
        }

        Location startLocation = player.getLocation().clone();
        afkStartLocations.put(uuid, startLocation);
        player.sendMessage("§6You are now AFK. §7Stay still for §61 minute§7 to earn a token.");

        int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player stillPlayer = Bukkit.getPlayer(uuid);
            if (stillPlayer == null) return;

            Location current = stillPlayer.getLocation();
            Location original = afkStartLocations.get(uuid);

            if (original != null &&
                    current.getBlockX() == original.getBlockX() &&
                    current.getBlockY() == original.getBlockY() &&
                    current.getBlockZ() == original.getBlockZ()) {

                TokenManager.giveTokens(uuid, 1);
                stillPlayer.sendMessage("§6+1 Token! §7Thanks for being AFK.");
            } else {
                stillPlayer.sendMessage("§cYou moved! §7No token for you.");
            }

            cancelAfk(uuid);
        }, 20L * 60).getTaskId(); // 1 minute later

        afkTasks.put(uuid, taskId);
        return true;
    }

    private void cancelAfk(UUID uuid) {
        Integer taskId = afkTasks.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        afkStartLocations.remove(uuid);
    }
}
