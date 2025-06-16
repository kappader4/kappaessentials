package net.kappasmp.kappaessentials.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Routes inventory click events to the Shop GUI handler if applicable.
 */
public class ShopGuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Prevent null pointers and ensure it's a relevant GUI
        if (event.getClickedInventory() == null || event.getView() == null) return;

        ShopGui.handleClick(event);
    }
}