package net.kappasmp.kappaessentials.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TaskScheduler {

    private static final Map<UUID, ScheduledTeleport> activeTeleports = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;

    public TaskScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void scheduleTeleport(Player player, int delayTicks, Runnable action, String homeName) {
        UUID uuid = player.getUniqueId();
        Location startLoc = player.getLocation().clone();

        ScheduledTeleport task = new ScheduledTeleport(delayTicks, action, startLoc, () -> {
            player.sendMessage("§cTeleport to §e" + homeName + "§c cancelled: you moved.");
        });

        activeTeleports.put(uuid, task);
    }

    public void scheduleMessage(Player player, int delayTicks, Runnable action) {
        Bukkit.getScheduler().runTaskLater(plugin, action, delayTicks);
    }

    public static void tickSafe() {
        for (UUID uuid : activeTeleports.keySet()) {
            ScheduledTeleport current = activeTeleports.get(uuid);
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                activeTeleports.remove(uuid);
                continue;
            }

            if (!player.getLocation().getBlock().equals(current.startLocation.getBlock())) {
                current.cancelMessage.run();
                activeTeleports.remove(uuid);
                continue;
            }

            current.delay--;
            if (current.delay <= 0) {
                current.action.run();
                activeTeleports.remove(uuid);
            }
        }
    }

    private static class ScheduledTeleport {
        int delay;
        Runnable action;
        Location startLocation;
        Runnable cancelMessage;

        ScheduledTeleport(int delay, Runnable action, Location startLocation, Runnable cancelMessage) {
            this.delay = delay;
            this.action = action;
            this.startLocation = startLocation;
            this.cancelMessage = cancelMessage;
        }
    }
}
