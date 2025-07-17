package net.kappasmp.kappaessentials.token;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokensCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        int tokens = TokenManager.getTokens(player.getUniqueId());
        player.sendMessage(ChatColor.GRAY + "Your token balance: " + ChatColor.GOLD + tokens);
        return true;
    }
}
