package net.kappasmp.kappaessentials.economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount>");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't pay yourself!");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount must be a positive number.");
            return true;
        }

        UUID senderUUID = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        int senderBalance = BalanceManager.getBalance(senderUUID);
        if (senderBalance < amount) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money!");
            return true;
        }

        // Perform transaction
        BalanceManager.addBalance(senderUUID, -amount);
        BalanceManager.addBalance(targetUUID, amount);

        sender.sendMessage(ChatColor.GREEN + "You paid " + ChatColor.YELLOW + target.getName() +
                ChatColor.GREEN + " $" + amount);
        target.sendMessage(ChatColor.GREEN + "You received $" + amount + " from " +
                ChatColor.YELLOW + player.getName());

        return true;
    }
}
