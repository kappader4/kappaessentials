package net.kappasmp.kappaessentials.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RtpGuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        RtpGui.handleClick(event);
    }
}
