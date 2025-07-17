package net.kappasmp.kappaessentials.command;

import net.kappasmp.kappaessentials.economy.BalanceManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class BalTopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        sender.sendMessage("§6§lTop 10 Balances:");

        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : BalanceManager.getTopBalances(10)) {
            String name = getName(entry.getKey());
            String formatted = formatBalance(entry.getValue());
            sender.sendMessage("§6§l" + rank + ". §7" + name + " - §a$" + formatted);
            rank++;
        }

        return true;
    }

    private String getName(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String name = player.getName();
        return name != null ? name : uuid.toString().substring(0, 8);
    }

    private String formatBalance(int balance) {
        double value = balance;
        if (value >= 1_000_000_000) return String.format("%.2fb", value / 1_000_000_000);
        if (value >= 1_000_000) return String.format("%.2fm", value / 1_000_000);
        if (value >= 1_000) return String.format("%.2fk", value / 1_000);
        return String.format("%.2f", value);
    }
}
