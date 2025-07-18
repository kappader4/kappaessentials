package net.kappasmp.kappaessentials.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /pay <player> <amount>");
            return true;
        }

        String targetName = args[0];
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cAmount must be a valid number.");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage("§cAmount must be greater than zero.");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage("§cYou can't pay yourself!");
            return true;
        }

        UUID senderUUID = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        int senderBalance = BalanceManager.getBalance(senderUUID);
        if (senderBalance < amount) {
            sender.sendMessage("§cYou don't have enough money!");
            return true;
        }

        // Transfer
        BalanceManager.subtractBalance(senderUUID, amount);
        BalanceManager.addBalance(targetUUID, amount);

        sender.sendMessage("§aYou paid §e" + target.getName() + " §a$" + amount);
        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage("§aYou received §a$" + amount + " §afrom §e" + player.getName());
        }

        return true;
    }
}
