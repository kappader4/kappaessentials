package net.kappasmp.kappaessentials.gui;

import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.model.ShopModels;
import net.kappasmp.kappaessentials.token.TokenManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ShopSubGui {

    private static final Map<UUID, Map<Integer, ShopModels.ShopItem>> shopContents = new HashMap<>();
    private static final String GUI_PREFIX = ChatColor.DARK_GRAY + "> ";

    public static void open(Player player, String shopId) {
        Optional<ShopModels.ShopShop> optional = ShopManager.getShopById(shopId);
        if (optional.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Shop not found.");
            return;
        }

        ShopModels.ShopShop shop = optional.get();
        Inventory gui = Bukkit.createInventory(null, 27, GUI_PREFIX + ChatColor.RESET + shop.getTitle());
        Map<Integer, ShopModels.ShopItem> slotMap = new HashMap<>();

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }

        for (int i = 0; i < 27; i++) {
            gui.setItem(i, filler);
        }

        for (ShopModels.ShopItem item : shop.getItems()) {
            Material mat = Material.matchMaterial(item.getId().replace("minecraft:", "").toUpperCase());
            if (mat == null) {
                System.err.println("[ShopSubGui] Invalid material: " + item.getId());
                continue;
            }

            ItemStack display = new ItemStack(mat, Math.min(item.getAmount(), mat.getMaxStackSize()));
            ItemMeta meta = display.getItemMeta();
            if (meta == null) continue;

            String name = item.getCustomName() != null ? item.getCustomName() : mat.name();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

            List<String> lore = new ArrayList<>();
            if (item.getCustomLore() != null && !item.getCustomLore().isEmpty()) {
                for (String line : item.getCustomLore()) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            } else {
                boolean isToken = "tokens".equalsIgnoreCase(shop.getCurrency());
                lore.add(ChatColor.GRAY + "Click to purchase");
                lore.add((isToken ? ChatColor.LIGHT_PURPLE + "Token Cost: " : ChatColor.GOLD + "💰 Cost: ") +
                        ChatColor.YELLOW + item.getPrice());
                lore.add(ChatColor.GOLD + "Amount: " + ChatColor.YELLOW + item.getAmount());
            }

            meta.setLore(lore);
            display.setItemMeta(meta);

            gui.setItem(item.getSlot(), display);
            slotMap.put(item.getSlot(), item);
        }

        shopContents.put(player.getUniqueId(), slotMap);
        player.openInventory(gui);
    }

    public static void handleClick(Player player, int slot) {
        Map<Integer, ShopModels.ShopItem> playerShop = shopContents.get(player.getUniqueId());
        if (playerShop == null || !playerShop.containsKey(slot)) return;

        ShopModels.ShopItem item = playerShop.get(slot);
        if (item == null) return;

        boolean isToken = "tokens".equalsIgnoreCase(item.getCurrency());

        boolean paid = isToken
                ? TokenManager.withdrawTokens(player.getUniqueId(), item.getPrice())
                : BalanceManager.withdrawBalance(player.getUniqueId(), item.getPrice());

        if (!paid) {
            player.sendMessage(ChatColor.RED + "You do not have enough " + (isToken ? "Tokens" : "Money") + ".");
            return;
        }

        String command;

        if (item.getCustomCommand() != null && !item.getCustomCommand().isEmpty()) {
            // Replace placeholder
            command = item.getCustomCommand().replace("%player%", player.getName());

            // Remove leading slash if present
            if (command.startsWith("/")) {
                command = command.substring(1);
            }

            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Failed to execute command.");
                Bukkit.getLogger().warning("[KappaEssentials] Failed to execute command: " + command);
                e.printStackTrace();
                return;
            }

        } else {
            // Fallback: give item via /give
            Material mat = Material.matchMaterial(item.getId().replace("minecraft:", "").toUpperCase());
            if (mat == null) {
                player.sendMessage(ChatColor.RED + "Invalid item ID: " + item.getId());
                return;
            }

            int amount = Math.min(item.getAmount(), mat.getMaxStackSize());
            command = "give " + player.getName() + " " + mat.name().toLowerCase() + " " + amount;

            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Failed to give item.");
                Bukkit.getLogger().warning("[KappaEssentials] Failed to run fallback give command: " + command);
                e.printStackTrace();
                return;
            }
        }

        player.sendMessage(ChatColor.GREEN + "Purchase successful!");
    }

    public static boolean isSubShopInventory(String title) {
        return title != null && title.startsWith(GUI_PREFIX) && !title.equals(GUI_PREFIX + "Shop");
    }
}