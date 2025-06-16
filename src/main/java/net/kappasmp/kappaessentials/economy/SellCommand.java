package net.kappasmp.kappaessentials.economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("inventory")) {
            return sellInventory(player);
        }

        return sellMainHand(player);
    }

    private boolean sellMainHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.GRAY + "You're not holding any item to sell.");
            return true;
        }

        String itemId = item.getType().getKey().toString(); // e.g. minecraft:diamond
        int count = item.getAmount();
        int pricePerItem = BalanceManager.getItemPrice(itemId);

        if (pricePerItem <= 0) {
            player.sendMessage(ChatColor.GRAY + "You cannot sell this item.");
            return true;
        }

        int totalValue = pricePerItem * count;
        item.setAmount(0); // remove the item
        BalanceManager.addBalance(player.getUniqueId(), totalValue);

        player.sendMessage(ChatColor.GRAY + "Sold " + ChatColor.GOLD + count + "x " +
                item.getType().name().toLowerCase() + ChatColor.GRAY + " for " +
                ChatColor.GOLD + "$" + totalValue);

        return true;
    }

    private boolean sellInventory(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        int totalValue = 0;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item != null && item.getType() != Material.AIR) {
                String itemId = item.getType().getKey().toString();
                int pricePerItem = BalanceManager.getItemPrice(itemId);

                if (pricePerItem <= 0) continue;

                int value = pricePerItem * item.getAmount();
                totalValue += value;
                contents[i] = null; // clear the item
            }
        }

        player.getInventory().setContents(contents);

        if (totalValue > 0) {
            BalanceManager.addBalance(player.getUniqueId(), totalValue);
            player.sendMessage(ChatColor.GRAY + "Sold all items in your inventory for " +
                    ChatColor.GOLD + "$" + totalValue);
        } else {
            player.sendMessage(ChatColor.GRAY + "You don't have any sellable items in your inventory.");
        }

        return true;
    }
}
