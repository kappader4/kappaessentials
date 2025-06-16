package net.kappasmp.kappaessentials.gui;

import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.model.ShopModels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopGui {

    private static final Map<String, ShopModels.ShopCategory> categoryByName = new HashMap<>();
    private static final String GUI_TITLE = ChatColor.DARK_GRAY + "> Shop";

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);
        categoryByName.clear();

        List<ShopModels.ShopCategory> categories = ShopManager.getMainMenu();
        if (categories == null) {
            player.sendMessage(ChatColor.RED + "Shop is not configured correctly.");
            return;
        }

        for (ShopModels.ShopCategory category : categories) {
            try {
                Material material = Material.matchMaterial(category.getIcon().toUpperCase());
                if (material == null) throw new IllegalArgumentException("Invalid material: " + category.getIcon());

                ItemStack icon = new ItemStack(material);
                ItemMeta meta = icon.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.RESET + category.getName());
                    icon.setItemMeta(meta);
                }

                gui.setItem(category.getSlot(), icon);
                categoryByName.put(ChatColor.stripColor(category.getName()), category);

            } catch (Exception e) {
                System.err.println("[ShopGui] Error loading category '" + category.getName() + "': " + e.getMessage());
            }
        }

        player.openInventory(gui);
    }

    public static void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        ShopModels.ShopCategory category = categoryByName.get(name);
        if (category == null) return;

        player.closeInventory();
        player.sendMessage(ChatColor.GRAY + "Opening " + ChatColor.YELLOW + category.getName() + ChatColor.GRAY + "...");
        ShopSubGui.open(player, category.getShopId());
    }
}
