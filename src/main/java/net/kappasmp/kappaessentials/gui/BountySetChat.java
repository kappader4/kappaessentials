package net.kappasmp.kappaessentials.gui;

import net.kappasmp.kappaessentials.bounty.BountyManager;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class BountySetChat {

    public static final HashMap<UUID, UUID> awaitingInput = new HashMap<>();

    public static void handleChat(AsyncPlayerChatEvent event) {
        UUID senderId = event.getPlayer().getUniqueId();
        if (!awaitingInput.containsKey(senderId)) return;

        event.setCancelled(true);

        Player sender = event.getPlayer();
        UUID targetId = awaitingInput.remove(senderId);

        try {
            int amount = Integer.parseInt(event.getMessage());
            if (amount <= 0) throw new NumberFormatException();

            if (BalanceManager.getBalance(senderId) < amount) {
                sender.sendMessage("§cNot enough money to set that bounty.");
                return;
            }

            BountyManager.setBounty(targetId, senderId, amount);
            BalanceManager.subtractBalance(senderId, amount);

            sender.sendMessage("§aBounty of §c$" + amount + " §aset on §e" +
                    Bukkit.getOfflinePlayer(targetId).getName());
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number. Try again.");
        }
    }
}
