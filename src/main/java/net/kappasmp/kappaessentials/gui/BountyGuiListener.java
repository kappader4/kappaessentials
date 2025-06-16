package net.kappasmp.kappaessentials.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

public class BountyGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().contains("Bounties")) return;

        event.setCancelled(true); // Prevent item grabbing

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) return;

        String title = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (title.equalsIgnoreCase("➕ Set New Bounty")) {
            player.closeInventory();
            SelectBountyTargetGui.open(player);
        }
    }
}
