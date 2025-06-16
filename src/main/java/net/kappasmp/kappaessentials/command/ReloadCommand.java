package net.kappasmp.kappaessentials.command;

import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.kappasmp.kappaessentials.homes.HomeManager;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.token.TokenManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kappaessentials.reload")) {
            sender.sendMessage("§cYou don't have permission to run this command.");
            return true;
        }

        try {
            // Reload all configs and data files
            ConfigManager.load();
            BalanceManager.reloadBalances();
            BalanceManager.reloadPrices();
            ShopManager.load();
            HomeManager.loadHomes();
            TokenManager.load();

            int categoryCount = ShopManager.getMainMenu().size();
            int shopCount = ShopManager.getShops().size();

            sender.sendMessage("§aKappaEssentials reloaded successfully.");
            sender.sendMessage("§7Loaded §e" + categoryCount + " §7categories and §e" + shopCount + " §7shops.");
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("§cAn error occurred while reloading. Check the console for more details.");
        }

        return true;
    }
}