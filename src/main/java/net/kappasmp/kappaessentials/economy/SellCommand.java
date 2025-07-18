package net.kappasmp.kappaessentials.economy;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SellCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("inventory")) {
            return sellInventory(player);
        } else {
            return sellMainHand(player);
        }
    }

    private boolean sellMainHand(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem == null || heldItem.getType() == Material.AIR) {
            player.sendMessage("§7You're not holding any item to sell.");
            return true;
        }

        String itemId = "minecraft:" + heldItem.getType().name().toLowerCase(Locale.ROOT);
        int count = heldItem.getAmount();
        int pricePerItem = BalanceManager.getItemPrice(itemId);

        if (pricePerItem <= 0) {
            player.sendMessage("§7You cannot sell this item.");
            return true;
        }

        int total = pricePerItem * count;
        heldItem.setAmount(0);
        BalanceManager.addBalance(player.getUniqueId(), total);

        player.sendMessage("§7Sold §e" + count + "x " + heldItem.getType().name().toLowerCase(Locale.ROOT) +
                " §7for §a$" + total);
        return true;
    }

    private boolean sellInventory(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        int totalValue = 0;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() == Material.AIR) continue;

            String itemId = "minecraft:" + item.getType().name().toLowerCase(Locale.ROOT);
            int pricePerItem = BalanceManager.getItemPrice(itemId);

            if (pricePerItem <= 0) continue;

            int itemTotal = pricePerItem * item.getAmount();
            totalValue += itemTotal;

            contents[i].setAmount(0); // clear the slot
        }

        if (totalValue > 0) {
            BalanceManager.addBalance(player.getUniqueId(), totalValue);
            player.sendMessage("§7Sold all items in your inventory for §a$" + totalValue);
        } else {
            player.sendMessage("§7You don't have any sellable items in your inventory.");
        }

        return true;
    }
}
