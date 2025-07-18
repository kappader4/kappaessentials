package net.kappasmp.kappaessentials;

import net.kappasmp.kappaessentials.bounty.BountyCommand;
import net.kappasmp.kappaessentials.bounty.BountyManager;
import net.kappasmp.kappaessentials.bounty.BountyRewardHandler;
import net.kappasmp.kappaessentials.command.*;
import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.economy.*;
import net.kappasmp.kappaessentials.gui.ShopGui;
import net.kappasmp.kappaessentials.homes.HomeCommand;
import net.kappasmp.kappaessentials.homes.HomeManager;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.teleport.*;
import net.kappasmp.kappaessentials.token.*;
import net.kappasmp.kappaessentials.update.ModUpdateChecker;
import net.kappasmp.kappaessentials.util.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class KappaEssentials extends JavaPlugin {

    private static KappaEssentials instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Loading KappaEssentials...");

        // Config and Data
        ConfigManager.init(this, getDataFolder().toPath());
        BalanceManager.init(this, getDataFolder().toPath());
        TokenManager.load();
        ShopManager.init(getDataFolder());
        HomeManager.loadHomes();
        BountyManager.loadBounties();

        // Tasks
        Bukkit.getScheduler().runTaskTimer(this, TaskScheduler::tickSafe, 1L, 1L);

        // Commands
        registerCommand("bal", new BalCommand());
        registerCommand("pay", new PayCommand());
        registerCommand("sell", new SellCommand());
        getCommand("reload").setExecutor(new ReloadCommand(this));
        getCommand("afk").setExecutor(new AfkCommand(this));
        registerCommand("tokens", new TokensCommand());
        registerCommand("tokentop", new TokenTopCommand());
        registerCommand("baltop", new BalTopCommand());
        getCommand("bounty").setExecutor(new BountyCommand(this));
        registerCommand("tpa", new TpaCommand());
        registerCommand("tpahere", new TpahereCommand());
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this));
        registerCommand("tpdeny", new TpDenyCommand());
        registerCommand("sethome", new HomeCommand());
        registerCommand("home", new HomeCommand());
        registerCommand("delhome", new HomeCommand());
        this.getCommand("shop").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cOnly players can use this command.");
                return true;
            }
            ShopGui.open(player);
            return true;
        });
        // Misc
        BountyRewardHandler.register();
        ModUpdateChecker.checkForUpdates("1.0-paper-1.21");

        getLogger().info("KappaEssentials loaded.");
    }

    @Override
    public void onDisable() {
        BalanceManager.saveBalances();
        TokenManager.save();
        ShopManager.saveShop();
        getLogger().info("KappaEssentials shut down.");
    }

    private void registerCommand(String name, Object handler) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) {
            getLogger().warning("Command not found in plugin.yml: " + name);
            return;
        }

        if (handler instanceof org.bukkit.command.CommandExecutor exec)
            cmd.setExecutor(exec);
        if (handler instanceof org.bukkit.command.TabCompleter tab)
            cmd.setTabCompleter(tab);
    }

    public static KappaEssentials getInstance() {
        return instance;
    }
}
