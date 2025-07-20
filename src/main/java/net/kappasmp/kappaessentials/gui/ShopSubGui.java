package net.kappasmp.kappaessentials.gui;

import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.model.ShopItem;
import net.kappasmp.kappaessentials.model.ShopShop;
import net.kappasmp.kappaessentials.token.TokenManager;
import net.kappasmp.kappaessentials.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopSubGui implements Listener {

    private static final Map<UUID, Map<Integer, ShopItem>> slotMappings = new HashMap<>();
    private static final Map<UUID, String> playerShopIds = new HashMap<>();

    public static void open(Player player, String shopId) {
        Optional<ShopShop> optionalShop = ShopManager.getShopById(shopId);
        if (optionalShop.isEmpty()) {
            player.sendMessage("§8Shop not found.");
            return;
        }

        ShopShop shop = optionalShop.get();
        Inventory inv = Bukkit.createInventory(null, 27, shop.title);
        Map<Integer, ShopItem> slotMap = new HashMap<>();

        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .build();
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        for (ShopItem item : shop.items) {
            NamespacedKey key = NamespacedKey.fromString(item.id);
            Material material = (key != null) ? Registry.MATERIAL.get(key) : null;

            if (material == null) {
                System.err.println("[ShopSubGui] Invalid item ID: " + item.id);
                continue;
            }

            String name = (item.customName != null && !item.customName.isEmpty())
                    ? item.customName
                    : "§7" + material.name().toLowerCase().replace("_", " ");

            ItemBuilder builder = new ItemBuilder(material).setName(name);

            if (item.customLore != null && !item.customLore.isEmpty()) {
                item.customLore.forEach(builder::addLore);
            } else {
                builder.addLore("§8Click to purchase");
                builder.addLore("§7" + (shop.currency.equalsIgnoreCase("tokens") ? "Token Cost" : "Cost") + ": §f" + item.price);
                builder.addLore("§7Amount: §f" + item.amount);
            }

            inv.setItem(item.slot, builder.build());
            slotMap.put(item.slot, item);
        }

        slotMappings.put(player.getUniqueId(), slotMap);
        playerShopIds.put(player.getUniqueId(), shopId);
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        UUID uuid = player.getUniqueId();
        if (!playerShopIds.containsKey(uuid)) return;

        String shopId = playerShopIds.get(uuid);
        Optional<ShopShop> optionalShop = ShopManager.getShopById(shopId);
        if (optionalShop.isEmpty()) return;
        ShopShop shop = optionalShop.get();

        if (!event.getView().getTitle().equals(shop.title)) return;

        event.setCancelled(true); // prevent stealing

        int slot = event.getRawSlot();
        if (!slotMappings.containsKey(uuid)) return;
        ShopItem item = slotMappings.get(uuid).get(slot);
        if (item == null) return;

        boolean paid = shop.currency.equalsIgnoreCase("tokens")
                ? TokenManager.withdrawTokens(uuid, item.price)
                : BalanceManager.withdrawBalance(uuid, item.price);

        if (!paid) {
            player.sendMessage("§8You do not have enough " +
                    (shop.currency.equalsIgnoreCase("tokens") ? "Tokens" : "Money") + ".");
            return;
        }

        String command = (item.customCommand != null && !item.customCommand.isEmpty())
                ? item.customCommand.replace("%player%", player.getName())
                : "give " + player.getName() + " " + item.id + " " + item.amount;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        player.sendMessage("§7Purchase successful.");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        UUID uuid = ((Player) event.getWhoClicked()).getUniqueId();
        if (!playerShopIds.containsKey(uuid)) return;

        String shopId = playerShopIds.get(uuid);
        Optional<ShopShop> optionalShop = ShopManager.getShopById(shopId);
        if (optionalShop.isEmpty()) return;
        ShopShop shop = optionalShop.get();

        if (!event.getView().getTitle().equals(shop.title)) return;

        int topSize = event.getView().getTopInventory().getSize();
        for (int slot : event.getRawSlots()) {
            if (slot < topSize) {
                event.setCancelled(true);
                return;
            }
        }
    }

    public static void clearPlayerCache(UUID uuid) {
        slotMappings.remove(uuid);
        playerShopIds.remove(uuid);
    }
}