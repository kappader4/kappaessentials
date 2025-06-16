package net.kappasmp.kappaessentials.command;

import net.kappasmp.kappaessentials.token.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class AfkCommand implements CommandExecutor {

    private static final Map<UUID, Location> afkStartPositions = new HashMap<>();
    private static final Map<UUID, Integer> afkTaskIds = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UUID uuid = player.getUniqueId();

        // Handle /afk stop
        if (args.length > 0 && args[0].equalsIgnoreCase("stop")) {
            if (!afkTaskIds.containsKey(uuid)) {
                player.sendMessage("§cYou're not AFK.");
                return true;
            }

            cancelAfk(uuid);
            player.sendMessage("§7You are no longer AFK.");
            return true;
        }

        // Already AFK?
        if (afkTaskIds.containsKey(uuid)) {
            player.sendMessage("§cYou're already AFK! Use §e/afk stop §cto cancel.");
            return true;
        }

        Location startLocation = player.getLocation().clone();
        afkStartPositions.put(uuid, startLocation);
        player.sendMessage("§6You are now AFK. §7Stay still to earn tokens every §61 minute§7.");

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                Bukkit.getPluginManager().getPlugin("KappaEssentials"),
                () -> {
                    Player stillPlayer = Bukkit.getPlayer(uuid);
                    if (stillPlayer == null || !stillPlayer.isOnline()) {
                        cancelAfk(uuid);
                        return;
                    }

                    Location currentLocation = stillPlayer.getLocation();
                    Location originalLocation = afkStartPositions.get(uuid);

                    // Check movement
                    if (hasMoved(originalLocation, currentLocation)) {
                        stillPlayer.sendMessage("§cYou moved! §7AFK cancelled.");
                        cancelAfk(uuid);
                        return;
                    }

                    // Give reward
                    TokenManager.giveTokens(uuid, 1);
                    stillPlayer.sendMessage("§6+1 Token! §7Thanks for being AFK.");
                },
                20 * 60, // initial delay: 60 seconds
                20 * 60  // repeat every 60 seconds
        );

        afkTaskIds.put(uuid, taskId);
        return true;
    }

    private void cancelAfk(UUID uuid) {
        Integer taskId = afkTaskIds.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        afkStartPositions.remove(uuid);
    }

    private boolean hasMoved(Location original, Location current) {
        return original.getBlockX() != current.getBlockX()
                || original.getBlockY() != current.getBlockY()
                || original.getBlockZ() != current.getBlockZ();
    }
}
