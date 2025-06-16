package net.kappasmp.kappaessentials.token;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TokensCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        UUID uuid = player.getUniqueId();
        int tokens = TokenManager.getTokens(uuid);
        player.sendMessage(ChatColor.GRAY + "Your token balance: " + ChatColor.GOLD + tokens);
        return true;
    }
}
