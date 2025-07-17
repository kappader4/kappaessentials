package net.kappasmp.kappaessentials.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cYou must specify a player name when using this command from console.");
                return true;
            }

            UUID uuid = player.getUniqueId();
            sendBalance(sender, uuid, player.getName());
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID uuid = target.getUniqueId();

        if (target.hasPlayedBefore() || target.isOnline()) {
            sendBalance(sender, uuid, targetName);
        } else {
            sender.sendMessage("§cPlayer not found.");
        }

        return true;
    }

    private void sendBalance(CommandSender sender, UUID uuid, String name) {
        int balance = BalanceManager.getBalance(uuid);
        String formatted = formatBalance(balance);

        sender.sendMessage("§7Balance of §6" + name + " §7is §a$" + formatted);
    }

    private String formatBalance(int balance) {
        if (balance >= 1_000_000_000) return String.format("%.1fB", balance / 1_000_000_000.0);
        if (balance >= 1_000_000) return String.format("%.1fM", balance / 1_000_000.0);
        if (balance >= 1_000) return String.format("%.1fK", balance / 1_000.0);
        return String.valueOf(balance);
    }
}
