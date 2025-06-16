package net.kappasmp.kappaessentials.homes;

import net.kappasmp.kappaessentials.homes.HomeManager.HomeData;
import net.kappasmp.kappaessentials.util.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HomeCommand implements CommandExecutor {

    private static final int TELEPORT_DELAY_SECONDS = 5;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /home <name>, /sethome <name>, or /delhome <name>");
            return true;
        }

        String subCommand = label.toLowerCase();
        String homeName = args[0].toLowerCase();

        switch (subCommand) {
            case "home" -> teleportToHome(player, homeName);
            case "sethome" -> setHome(player, homeName);
            case "delhome" -> deleteHome(player, homeName);
            default -> player.sendMessage("§cUnknown command.");
        }

        return true;
    }

    private void teleportToHome(Player player, String homeName) {
        HomeData home = HomeManager.getHome(player, homeName);
        if (home == null) {
            player.sendMessage("§cHome not found: " + homeName);
            return;
        }

        World world = Bukkit.getWorld(home.getWorld());
        if (world == null) {
            player.sendMessage("§cWorld not found: " + home.getWorld());
            return;
        }

        Location location = new Location(world, home.getX() + 0.5, home.getY(), home.getZ() + 0.5);

        for (int second = 1; second <= TELEPORT_DELAY_SECONDS; second++) {
            int timeLeft = TELEPORT_DELAY_SECONDS - second + 1;
            int delayTicks = second * 20;

            TaskScheduler.scheduleMessage(player, delayTicks, () ->
                    player.sendMessage("§7Teleporting in §e" + timeLeft + "§7...")
            );
        }

        TaskScheduler.scheduleTeleport(player, TELEPORT_DELAY_SECONDS * 20, () -> {
            CompletableFuture.runAsync(() -> world.loadChunk(location.getChunk()))
                    .thenRun(() -> {
                        player.teleport(location);
                        player.sendMessage("§aTeleported to home: " + homeName);
                    });
        }, homeName);
    }

    private void setHome(Player player, String homeName) {
        if (HomeManager.getHome(player, homeName) != null) {
            player.sendMessage("§cHome already exists: " + homeName);
            return;
        }

        Location loc = player.getLocation();
        HomeData home = new HomeData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
        HomeManager.setHome(player, homeName, home);
        player.sendMessage("§aHome '" + homeName + "' has been set.");
    }

    private void deleteHome(Player player, String homeName) {
        Map<String, HomeData> homes = HomeManager.getHomes(player);
        if (!homes.containsKey(homeName)) {
            player.sendMessage("§cHome not found: " + homeName);
            return;
        }

        HomeManager.removeHome(player, homeName);
        player.sendMessage("§aHome '" + homeName + "' has been deleted.");
    }
}
