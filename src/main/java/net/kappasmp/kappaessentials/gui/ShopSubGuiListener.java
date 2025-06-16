package net.kappasmp.kappaessentials.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopSubGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        if (!ShopSubGui.isSubShopInventory(title)) return;
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;

        event.setCancelled(true);
        int slot = event.getSlot();
        ShopSubGui.handleClick(player, slot);
    }
}
