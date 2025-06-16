package net.kappasmp.kappaessentials.gui;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.SkullMeta;

public class SelectBountyTargetGui {

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "> Select Target");

        int slot = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getUniqueId().equals(player.getUniqueId())) continue;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(target);
            meta.setDisplayName(ChatColor.GOLD + "☠ " + target.getName());
            meta.setLore(List.of(ChatColor.GRAY + "Click to place a bounty"));
            head.setItemMeta(meta);

            gui.setItem(slot++, head);
        }

        player.openInventory(gui);
    }
}
