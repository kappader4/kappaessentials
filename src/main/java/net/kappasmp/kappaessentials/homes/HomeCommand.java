package net.kappasmp.kappaessentials.homes;

import net.kappasmp.kappaessentials.homes.HomeManager.HomeData;
import net.kappasmp.kappaessentials.teleport.HomeTeleportScheduler;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class HomeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cUsage: /" + label + " <homeName>");
            return true;
        }

        String homeName = args[0];
        String lower = label.toLowerCase();

        if (lower.equals("home")) {
            HomeData home = HomeManager.getHome(player, homeName);
            if (home == null || home.location == null) {
                player.sendMessage("§cHome not found: " + homeName);
                return true;
            }

            HomeTeleportScheduler.queue(player, home.location, homeName);
            return true;

        } else if (lower.equals("sethome")) {
            Location loc = player.getLocation().clone();
            HomeData data = new HomeData(loc);

            boolean success = HomeManager.setHome(player, homeName, data);
            if (success) {
                player.sendMessage("§aHome '" + homeName + "' has been set.");
            } else {
                player.sendMessage("§cCould not set home. You may be over your limit, on cooldown, or cross-dimension teleporting is blocked.");
            }
            return true;

        } else if (lower.equals("delhome")) {
            boolean removed = HomeManager.removeHome(player, homeName);
            if (removed) {
                player.sendMessage("§aHome '" + homeName + "' has been deleted.");
            } else {
                player.sendMessage("§cHome not found: " + homeName);
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return Collections.emptyList();
        if (args.length == 1) {
            return new ArrayList<>(HomeManager.getHomes(player).keySet());
        }
        return Collections.emptyList();
    }

    public static void registerCommands(PluginCommand homeCmd, PluginCommand sethomeCmd, PluginCommand delhomeCmd) {
        HomeCommand handler = new HomeCommand();
        homeCmd.setExecutor(handler);
        homeCmd.setTabCompleter(handler);

        sethomeCmd.setExecutor(handler);
        sethomeCmd.setTabCompleter(handler);

        delhomeCmd.setExecutor(handler);
        delhomeCmd.setTabCompleter(handler);
    }
}
