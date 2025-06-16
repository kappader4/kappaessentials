package net.kappasmp.kappaessentials.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TaskScheduler {

    private static final Map<UUID, ScheduledTeleport> activeTeleports = new HashMap<>();
    private static final List<ScheduledMessage> scheduledMessages = new LinkedList<>();

    public static void scheduleTeleport(Player player, int delayTicks, Runnable action, String homeName) {
        Location currentLoc = player.getLocation().clone();

        activeTeleports.put(player.getUniqueId(), new ScheduledTeleport(
                delayTicks,
                action,
                currentLoc,
                ChatColor.RED + "Teleport to " + ChatColor.GOLD + homeName + ChatColor.RED + " cancelled: you moved."
        ));

        startTickTask(); // Ensures tick loop is running
    }

    public static void scheduleMessage(Player player, int delayTicks, Runnable action) {
        scheduledMessages.add(new ScheduledMessage(player.getUniqueId(), delayTicks, action));
        startTickTask(); // Ensures tick loop is running
    }

    private static boolean ticking = false;

    private static void startTickTask() {
        if (ticking) return;

        ticking = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                // Handle teleport tasks
                Iterator<Map.Entry<UUID, ScheduledTeleport>> teleportIterator = activeTeleports.entrySet().iterator();
                while (teleportIterator.hasNext()) {
                    Map.Entry<UUID, ScheduledTeleport> entry = teleportIterator.next();
                    Player player = Bukkit.getPlayer(entry.getKey());
                    ScheduledTeleport task = entry.getValue();

                    if (player == null || !player.isOnline()) {
                        teleportIterator.remove();
                        continue;
                    }

                    if (!player.getLocation().getBlock().equals(task.startLoc.getBlock())) {
                        player.sendMessage(task.cancelMessage);
                        teleportIterator.remove();
                        continue;
                    }

                    task.delay--;
                    if (task.delay <= 0) {
                        task.action.run();
                        teleportIterator.remove();
                    }
                }

                // Handle scheduled chat messages
                Iterator<ScheduledMessage> messageIterator = scheduledMessages.iterator();
                while (messageIterator.hasNext()) {
                    ScheduledMessage message = messageIterator.next();
                    message.delay--;

                    if (message.delay <= 0) {
                        Player player = Bukkit.getPlayer(message.playerId);
                        if (player != null && player.isOnline()) {
                            message.action.run();
                        }
                        messageIterator.remove();
                    }
                }

                if (activeTeleports.isEmpty() && scheduledMessages.isEmpty()) {
                    ticking = false;
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("KappaEssentials"), 1L, 1L);
    }

    private static class ScheduledTeleport {
        int delay;
        Runnable action;
        Location startLoc;
        String cancelMessage;

        ScheduledTeleport(int delay, Runnable action, Location startLoc, String cancelMessage) {
            this.delay = delay;
            this.action = action;
            this.startLoc = startLoc;
            this.cancelMessage = cancelMessage;
        }
    }

    private static class ScheduledMessage {
        UUID playerId;
        int delay;
        Runnable action;

        ScheduledMessage(UUID playerId, int delay, Runnable action) {
            this.playerId = playerId;
            this.delay = delay;
            this.action = action;
        }
    }
}
