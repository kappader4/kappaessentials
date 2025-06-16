package net.kappasmp.kappaessentials.gui;

import net.kappasmp.kappaessentials.bounty.BountyManager;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BountyInputHandler implements Listener {

    private static final Map<UUID, UUID> pendingBounties = new HashMap<>();

    public static void setPendingTarget(UUID player, UUID target) {
        pendingBounties.put(player, target);
    }

    @org.bukkit.event.EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!pendingBounties.containsKey(playerId)) return;

        event.setCancelled(true); // prevent broadcast
        UUID targetId = pendingBounties.remove(playerId);

        int amount;
        try {
            amount = Integer.parseInt(event.getMessage());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid bounty amount.");
            return;
        }

        if (BalanceManager.getBalance(playerId) < amount) {
            player.sendMessage(ChatColor.RED + "You don't have enough balance.");
            return;
        }

        BountyManager.setBounty(targetId, playerId, amount);
        BalanceManager.subtractBalance(playerId, amount);

        player.sendMessage(ChatColor.GREEN + "Bounty placed on " + ChatColor.YELLOW + Bukkit.getOfflinePlayer(targetId).getName()
                + ChatColor.GREEN + " for " + ChatColor.RED + "$" + amount);
    }
}
