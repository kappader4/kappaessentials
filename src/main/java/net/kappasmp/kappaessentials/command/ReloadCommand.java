package net.kappasmp.kappaessentials.command;

import com.mojang.brigadier.CommandDispatcher;
import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.homes.HomeManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("kappaessentials")
                .then(CommandManager.literal("reload")
                        .requires(source -> source.hasPermissionLevel(2)) // Only ops
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();

                            try {
                                // Reload config
                                ConfigManager.load();

                                // Reload data files
                                BalanceManager.loadBalances();
                                BalanceManager.loadItemPrices();
                                ShopManager.loadShop();
                                HomeManager.loadHomes();

                                int categories = ShopManager.getMainMenu().size();
                                int shops = ShopManager.getShopCount();

                                source.sendFeedback(() ->
                                        Text.literal("§aKappaEssentials reloaded successfully. §7Loaded §e" + categories + " §7categories and §e" + shops + " §7shops."), false);
                            } catch (Exception e) {
                                e.printStackTrace();
                                source.sendError(Text.literal("§cFailed to reload some components. Check console for details."));
                            }

                            return 1;
                        })
                )
        );
    }
}
