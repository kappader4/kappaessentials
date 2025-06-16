package net.kappasmp.kappaessentials.token;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class TokenTopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Top 10 Tokens:");

        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : TokenManager.getTopTokens(10)) {
            String name = getPlayerName(entry.getKey());
            String formatted = formatTokens(entry.getValue());

            sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + rank + ". " + ChatColor.GRAY + name + " - " + formatted + " Tokens");
            rank++;
        }
        return true;
    }

    private String getPlayerName(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player.getName() != null ? player.getName() : uuid.toString().substring(0, 8);
    }

    private String formatTokens(int tokens) {
        double value = tokens;
        if (value >= 1_000_000_000) {
            return String.format("%.2fb", value / 1_000_000_000);
        } else if (value >= 1_000_000) {
            return String.format("%.2fm", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.2fk", value / 1_000);
        } else {
            return String.format("%.0f", value); // No decimals
        }
    }
}
