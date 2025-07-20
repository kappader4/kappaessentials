package net.kappasmp.kappaessentials.gui;

import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.model.ShopCategory;
import net.kappasmp.kappaessentials.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ShopGui implements Listener {

    private static final Map<Integer, String> slotToShopId = new HashMap<>();
    private static final String GUI_TITLE = "§8> ѕʜᴏᴘ";

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        for (ShopCategory category : ShopManager.getMainMenu()) {
            NamespacedKey key = NamespacedKey.fromString(category.icon);
            Material icon = (key != null) ? Registry.MATERIAL.get(key) : null;

            if (icon == null) {
                System.err.println("[ShopGui] Invalid icon ID: " + category.icon);
                icon = Material.BARRIER;
            }

            ItemStack item = new ItemBuilder(icon)
                    .setName(category.name)
                    .build();

            gui.setItem(category.slot, item);
            slotToShopId.put(category.slot, category.shopId);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        // Cancel clicks in top inventory or shift-clicks
        if (clickedInventory == event.getView().getTopInventory() || event.isShiftClick()) {
            event.setCancelled(true);
        }

        int slot = event.getRawSlot();
        if (!slotToShopId.containsKey(slot)) return;

        // Open sub-shop
        String shopId = slotToShopId.get(slot);
        ShopSubGui.open(player, shopId);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        int topSize = event.getView().getTopInventory().getSize();
        for (int slot : event.getRawSlots()) {
            if (slot < topSize) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
