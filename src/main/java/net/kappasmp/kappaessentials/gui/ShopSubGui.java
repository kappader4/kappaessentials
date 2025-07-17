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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopSubGui {

    private static final Map<UUID, Map<Integer, ShopItem>> slotMappings = new HashMap<>();
    private static final Map<UUID, String> playerShopIds = new HashMap<>();

    public static void open(Player player, String shopId) {
        Optional<ShopShop> optionalShop = ShopManager.getShopById(shopId);
        if (optionalShop.isEmpty()) {
            player.sendMessage("§cShop not found.");
            return;
        }

        ShopShop shop = optionalShop.get();
        Inventory inv = Bukkit.createInventory(null, 27, shop.title);

        Map<Integer, ShopItem> slotMap = new HashMap<>();

        // Fill background
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
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
                    : "§f" + material.name().toLowerCase().replace("_", " ");

            ItemBuilder builder = new ItemBuilder(material)
                    .setName(name);

            if (item.customLore != null && !item.customLore.isEmpty()) {
                for (String line : item.customLore) {
                    builder.addLore(line);
                }
            } else {
                builder.addLore("§7Click to purchase");
                builder.addLore("§6" + (shop.currency.equalsIgnoreCase("tokens") ? "Token Cost" : "💰 Cost") + ": §e" + item.price);
                builder.addLore("§6Amount: §e" + item.amount);
            }

            inv.setItem(item.slot, builder.build());
            slotMap.put(item.slot, item);
        }

        slotMappings.put(player.getUniqueId(), slotMap);
        playerShopIds.put(player.getUniqueId(), shopId);
        player.openInventory(inv);
    }

    public static void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();

        if (!playerShopIds.containsKey(uuid)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(ShopManager.getShopById(playerShopIds.get(uuid)).map(s -> s.title).orElse(""))) return;

        event.setCancelled(true);

        int slot = event.getSlot();
        if (!slotMappings.containsKey(uuid)) return;

        ShopItem item = slotMappings.get(uuid).get(slot);
        if (item == null) return;

        String shopId = playerShopIds.get(uuid);
        ShopShop shop = ShopManager.getShopById(shopId).orElse(null);
        if (shop == null) return;

        boolean paid = shop.currency.equalsIgnoreCase("tokens")
                ? TokenManager.withdrawTokens(uuid, item.price)
                : BalanceManager.withdrawBalance(uuid, item.price);

        if (!paid) {
            player.sendMessage("§cYou do not have enough " + (shop.currency.equalsIgnoreCase("tokens") ? "Tokens" : "Money") + ".");
            return;
        }

        String command = (item.customCommand != null && !item.customCommand.isEmpty())
                ? item.customCommand.replace("%player%", player.getName())
                : "give " + player.getName() + " " + item.id + " " + item.amount;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        player.sendMessage("§aPurchase successful!");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

    public static void clearPlayerCache(UUID uuid) {
        slotMappings.remove(uuid);
        playerShopIds.remove(uuid);
    }
}
