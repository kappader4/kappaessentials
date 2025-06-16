package net.kappasmp.kappaessentials.gui;

import net.kappasmp.kappaessentials.bounty.BountyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class BountyGui {

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "> Bounties");

        List<UUID> sorted = BountyManager.getAllBountiedPlayers().stream()
                .sorted(Comparator.comparingInt(BountyManager::getBounty).reversed())
                .toList();

        if (sorted.isEmpty()) {
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta meta = barrier.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_GRAY + "No bounties yet!");
            meta.setLore(List.of(ChatColor.GRAY + "Use the anvil below to add one."));
            barrier.setItemMeta(meta);
            gui.setItem(22, barrier);
        } else {
            int slot = 0;
            for (UUID uuid : sorted) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                int amount = BountyManager.getBounty(uuid);
                String formatted = formatMoney(amount);

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(target);
                meta.setDisplayName(ChatColor.GOLD + "☠ " + target.getName());
                meta.setLore(List.of(ChatColor.GRAY + "Bounty: " + ChatColor.RED + "$" + formatted));
                head.setItemMeta(meta);

                gui.setItem(slot++, head);
            }
        }

        // Set bounty button
        ItemStack anvil = new ItemStack(Material.ANVIL);
        ItemMeta anvilMeta = anvil.getItemMeta();
        anvilMeta.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "➕ Set New Bounty");
        anvilMeta.setLore(List.of(ChatColor.GRAY + "Click to set a bounty on a player."));
        anvil.setItemMeta(anvilMeta);
        gui.setItem(49, anvil);

        player.openInventory(gui);
    }

    public static String formatMoney(int amount) {
        if (amount >= 1_000_000_000) return (amount / 1_000_000_000) + "B";
        if (amount >= 1_000_000) return (amount / 1_000_000) + "M";
        if (amount >= 1_000) return (amount / 1_000) + "K";
        return String.valueOf(amount);
    }
}
