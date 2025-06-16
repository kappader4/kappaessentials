package net.kappasmp.kappaessentials.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class HomeTeleportScheduler {
    private static final List<TeleportTask> tasks = new ArrayList<>();

    public static void tick() {
        Iterator<TeleportTask> iterator = tasks.iterator();

        while (iterator.hasNext()) {
            TeleportTask task = iterator.next();

            if (!task.player.getLocation().getBlock().equals(task.startLocation.getBlock())) {
                task.player.sendMessage("§cYou moved! Teleportation cancelled.");
                iterator.remove();
                continue;
            }

            if (task.delayTicks == 0) {
                World world = Bukkit.getWorld(task.targetLocation.getWorld().getUID());
                if (world == null) {
                    task.player.sendMessage("§cError: world not found!");
                    iterator.remove();
                    continue;
                }

                task.player.teleport(task.targetLocation);
                task.player.sendMessage("§aTeleported to home '" + task.name + "'!");
                iterator.remove();
                continue;
            }

            if (task.delayTicks % 20 == 0) {
                int secondsLeft = task.delayTicks / 20;
                task.player.sendMessage("§7Teleporting in " + secondsLeft + "...");
            }

            task.delayTicks--;
        }
    }

    public static void queue(Player player, Location targetLocation, String name) {
        tasks.add(new TeleportTask(player, targetLocation, name));
    }

    public static void startTickTask(org.bukkit.plugin.Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, 0L, 1L); // Runs every tick
    }

    private static class TeleportTask {
        Player player;
        Location targetLocation;
        String name;
        int delayTicks = 5 * 20;
        Location startLocation;

        TeleportTask(Player player, Location targetLocation, String name) {
            this.player = player;
            this.targetLocation = targetLocation;
            this.name = name;
            this.startLocation = player.getLocation().clone();
        }
    }
}
