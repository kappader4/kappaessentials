package net.kappasmp.kappaessentials.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.UUID;

public class BalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            // /bal - show sender's balance
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Only players can check their own balance.");
                return true;
            }
            UUID uuid = player.getUniqueId();
            return showBalance(sender, uuid, player.getName());
        }

        // /bal <player>
        String targetName = args[0];
        Player onlineTarget = Bukkit.getPlayerExact(targetName);

        if (onlineTarget != null) {
            // Target is online
            return showBalance(sender, onlineTarget.getUniqueId(), onlineTarget.getName());
        }

        // Fallback to offline player (deprecated method, but safe with check)
        @SuppressWarnings("deprecation")
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);

        if (!offlineTarget.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        return showBalance(sender, offlineTarget.getUniqueId(), offlineTarget.getName());
    }

    private boolean showBalance(CommandSender sender, UUID uuid, String name) {
        int balance = BalanceManager.getBalance(uuid);
        String formatted = formatBalance(balance);

        sender.sendMessage(ChatColor.GRAY + "Balance of " +
                ChatColor.GOLD + name +
                ChatColor.GRAY + " is " +
                ChatColor.GOLD + "$" + formatted);

        return true;
    }

    private String formatBalance(int balance) {
        if (balance >= 1_000_000_000) {
            return String.format("%.1fB", balance / 1_000_000_000.0);
        } else if (balance >= 1_000_000) {
            return String.format("%.1fM", balance / 1_000_000.0);
        } else if (balance >= 1_000) {
            return String.format("%.1fK", balance / 1_000.0);
        } else {
            return String.format("%d", balance);
        }
    }
}
