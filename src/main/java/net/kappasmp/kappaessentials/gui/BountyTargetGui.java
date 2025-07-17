package net.kappasmp.kappaessentials.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import net.kappasmp.kappaessentials.util.ItemBuilder;

public class BountyTargetGui {

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "> Select Target");

        int slot = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(player.getUniqueId())) continue;

            gui.setItem(slot++, new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullOwner(online)
                    .setName("§6☠ " + online.getName())
                    .addLore("§7Click to set bounty.")
                    .build());
        }

        player.openInventory(gui);
    }

    public static void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!event.getView().getTitle().contains("Select Target")) return;

        event.setCancelled(true);

        var item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName().replace("☠ ", ""));
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            player.sendMessage("§cThat player is no longer online.");
            return;
        }

        player.closeInventory();
        player.sendMessage("§7Enter bounty amount for §e" + name + "§7 in chat.");

        BountySetChat.awaitingInput.put(player.getUniqueId(), target.getUniqueId());
    }
}
