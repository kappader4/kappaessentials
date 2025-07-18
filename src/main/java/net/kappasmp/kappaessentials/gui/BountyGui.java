package net.kappasmp.kappaessentials.gui;

import net.kappasmp.kappaessentials.bounty.BountyManager;
import net.kappasmp.kappaessentials.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BountyGui {

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "> Bounties");

        Set<UUID> bountyTargets = BountyManager.getAllBountiedPlayers();
        if (bountyTargets.isEmpty()) {
            gui.setItem(22, new ItemBuilder(Material.BARRIER)
                    .setName("§7No bounties yet!")
                    .addLore("§7Use the anvil below to add one.")
                    .build());
        } else {
            List<UUID> sorted = bountyTargets.stream()
                    .sorted((a, b) -> Integer.compare(BountyManager.getBounty(b), BountyManager.getBounty(a)))
                    .toList();

            int slot = 0;
            for (UUID uuid : sorted) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                int bounty = BountyManager.getBounty(uuid);

                gui.setItem(slot++, new ItemBuilder(Material.PLAYER_HEAD)
                        .setSkullOwner(target)
                        .setName("§6☠ " + target.getName())
                        .addLore("§7Bounty: §c$" + BountyGui.formatMoney(bounty))
                        .build());
            }
        }

        gui.setItem(49, new ItemBuilder(Material.ANVIL)
                .setName("§a➕ Set New Bounty")
                .addLore("§7Click to set a bounty on a player.")
                .build());

        player.openInventory(gui);
    }

    public static void handleClick(InventoryClickEvent event) {
        HumanEntity clicker = event.getWhoClicked();
        if (!(clicker instanceof Player player)) return;

        if (!event.getView().getTitle().contains("Bounties")) return;

        event.setCancelled(true); // prevent item moving

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.ANVIL) {
            player.closeInventory();
            BountyTargetGui.open(player);
            return;
        }

        if (clicked.getType() == Material.PLAYER_HEAD) {
            String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().replace("☠ ", ""));
            player.sendMessage("§7Target: §e" + name + " §7has a bounty.");
        }
    }

    public static String formatMoney(int amount) {
        if (amount >= 1_000_000_000) return amount / 1_000_000_000 + "B";
        if (amount >= 1_000_000) return amount / 1_000_000 + "M";
        if (amount >= 1_000) return amount / 1_000 + "K";
        return String.valueOf(amount);
    }
}
