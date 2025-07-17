package net.kappasmp.kappaessentials.gui;

import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.model.ShopCategory;
import net.kappasmp.kappaessentials.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ShopGui {

    private static final Map<Integer, String> slotToShopId = new HashMap<>();

    public static void open(Player player) {
        // Deprecated, but still works — replace with Component.text("§8> ѕʜᴏᴘ") if using Adventure API
        Inventory gui = Bukkit.createInventory(null, 27, "§8> ѕʜᴏᴘ");

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

    public static void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        // Deprecated method: still usable; optional to switch to `Component` handling
        if (!event.getView().getTitle().contains("ѕʜᴏᴘ")) return;

        event.setCancelled(true);

        int slot = event.getSlot();
        if (!slotToShopId.containsKey(slot)) return;

        String shopId = slotToShopId.get(slot);
        ShopSubGui.open(player, shopId);
    }
}
