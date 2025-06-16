package net.kappasmp.kappaessentials.command;

import net.kappasmp.kappaessentials.economy.BalanceManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class BalTopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        var topBalances = BalanceManager.getTopBalances(10);

        sender.sendMessage("§6§lTop 10 Balances:");
        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : topBalances) {
            String name = getPlayerName(entry.getKey());
            String formatted = formatBalance(entry.getValue());
            sender.sendMessage("§6§l" + rank + ". §7" + name + " - $" + formatted);
            rank++;
        }
        return true;
    }

    private String getPlayerName(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player.getName() != null) {
            return player.getName();
        } else {
            return uuid.toString().substring(0, 8);
        }
    }

    private String formatBalance(int balance) {
        double value = balance;
        if (value >= 1_000_000_000) {
            return String.format("%.2fb", value / 1_000_000_000);
        } else if (value >= 1_000_000) {
            return String.format("%.2fm", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.2fk", value / 1_000);
        } else {
            return String.format("%.2f", value);
        }
    }
}
