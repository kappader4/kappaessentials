package net.kappasmp.kappaessentials.listeners;

import net.kappasmp.kappaessentials.economy.BalanceManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static net.kappasmp.kappaessentials.economy.BalanceManager.itemPrices;

public class InventoryWorthLoreListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() != InventoryType.PLAYER) return;

        ItemStack current = event.getCurrentItem();
        if (current == null || current.getType() == Material.AIR) return;

        ItemMeta meta = current.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        String worthLore = BalanceManager.getItemWorthLore(current.getType());

        // Avoid duplicate lore
        if (lore.stream().noneMatch(line -> line.contains("Worth:") || line.contains("Can't sell"))) {
            lore.add(worthLore);
            meta.setLore(lore);
            current.setItemMeta(meta);
        }
    }
}