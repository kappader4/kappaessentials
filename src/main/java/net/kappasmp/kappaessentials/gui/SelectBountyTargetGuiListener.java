package net.kappasmp.kappaessentials.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SelectBountyTargetGuiListener implements Listener {

    @EventHandler
    public void onTargetSelect(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().contains("Select Target")) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        if (clicked.getItemMeta() instanceof SkullMeta meta) {
            String name = meta.getOwningPlayer() != null ? meta.getOwningPlayer().getName() : null;
            if (name != null && !name.equals(player.getName())) {
                Player target = player.getServer().getPlayerExact(name);
                if (target != null) {
                    BountyInputHandler.setPendingTarget(player.getUniqueId(), target.getUniqueId());
                    player.closeInventory();
                    player.sendMessage(ChatColor.GRAY + "Type the bounty amount for " + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " in chat.");
                }
            }
        }
    }
}
