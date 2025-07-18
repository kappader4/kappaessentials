package net.kappasmp.kappaessentials.command;

import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.kappasmp.kappaessentials.homes.HomeManager;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.token.TokenManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final Plugin plugin;

    public ReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to do that.");
            return true;
        }

        try {
            ConfigManager.load();
            BalanceManager.loadBalances();
            BalanceManager.loadItemPrices();
            ShopManager.loadShop();
            HomeManager.loadHomes();
            TokenManager.load();

            int categories = ShopManager.getMainMenu().size();
            int shops = ShopManager.getShopCount();

            sender.sendMessage("§aKappaEssentials reloaded successfully. §7Loaded §e" + categories +
                    " §7categories and §e" + shops + " §7shops.");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to reload components:");
            e.printStackTrace();
            sender.sendMessage("§cFailed to reload some components. Check console for details.");
        }

        return true;
    }
}
