package net.kappasmp.kappaessentials.teleport;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kappasmp.kappaessentials.KappaEssentials;
import net.kappasmp.kappaessentials.config.HomeConfig;
import net.kappasmp.kappaessentials.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeTeleportScheduler {

    private static final Map<UUID, ScheduledTeleport> scheduledTeleports = new HashMap<>();

    public static void queue(Player player, Location target, String homeName) {
        HomeConfig config = ConfigManager.getHomeConfig();
        int delaySeconds = config.teleportDelaySeconds;

        ScheduledTeleport task = new ScheduledTeleport(player, target, homeName, delaySeconds);
        scheduledTeleports.put(player.getUniqueId(), task);
        task.runTaskTimer(KappaEssentials.getInstance(), 0L, 20L); // Every second
    }

    public static void cancel(Player player) {
        ScheduledTeleport task = scheduledTeleports.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    private static class ScheduledTeleport extends BukkitRunnable {
        private final Player player;
        private final Location startPos;
        private final Location targetPos;
        private final String homeName;
        private int countdown;

        ScheduledTeleport(Player player, Location target, String homeName, int delaySeconds) {
            this.player = player;
            this.targetPos = target;
            this.homeName = homeName;
            this.startPos = player.getLocation().clone();
            this.countdown = delaySeconds;
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                cancel();
                return;
            }

            // Cancel if player moved
            Location current = player.getLocation();
            if (!current.getBlock().equals(startPos.getBlock())) {
                player.sendMessage("§cYou moved! Teleportation cancelled.");
                cancel();
                scheduledTeleports.remove(player.getUniqueId());
                return;
            }

            if (countdown <= 0) {
                World world = Bukkit.getWorld(targetPos.getWorld().getUID());
                if (world == null) {
                    player.sendMessage("§cError: World not found.");
                    cancel();
                    return;
                }

                player.teleportAsync(targetPos).thenRun(() ->
                        player.sendMessage("§aTeleported to home §e" + homeName + "§a!")
                );

                cancel();
                scheduledTeleports.remove(player.getUniqueId());
                return;
            }

            player.sendMessage("§7Teleporting in " + countdown + "...");
            countdown--;
        }
    }
}
