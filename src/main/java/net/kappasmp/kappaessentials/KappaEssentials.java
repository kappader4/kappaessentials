package net.kappasmp.kappaessentials;

import net.kappasmp.kappaessentials.auction.AhSellCommand;
import net.kappasmp.kappaessentials.auction.AuctionGuiManager;
import net.kappasmp.kappaessentials.auction.AuctionManager;
import net.kappasmp.kappaessentials.bounty.BountyCommand;
import net.kappasmp.kappaessentials.bounty.BountyRewardHandler;
import net.kappasmp.kappaessentials.command.*;
import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.economy.*;
import net.kappasmp.kappaessentials.auction.*;
import net.kappasmp.kappaessentials.gui.BountyGuiListener;
import net.kappasmp.kappaessentials.gui.ShopGui;
import net.kappasmp.kappaessentials.gui.ShopSubGui;
import net.kappasmp.kappaessentials.homes.HomeCommand;
import net.kappasmp.kappaessentials.homes.HomeManager;
import net.kappasmp.kappaessentials.listener.AfkListener;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.placeholder.KappaPlaceholders;
import net.kappasmp.kappaessentials.teleport.*;
import net.kappasmp.kappaessentials.token.*;
import net.kappasmp.kappaessentials.update.ModUpdateChecker;
import net.kappasmp.kappaessentials.util.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kappasmp.kappaessentials.bounty.BountyManager.loadBounties;

public final class KappaEssentials extends JavaPlugin {

    private static KappaEssentials instance;
    private AuctionManager auctionManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Loading KappaEssentials...");
        this.auctionManager = new AuctionManager(getDataFolder());
        getCommand("ah").setExecutor(new AhSellCommand(auctionManager));
        getServer().getPluginManager().registerEvents(new ShopGui(), this);
        getServer().getPluginManager().registerEvents(new BountyGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new net.kappasmp.kappaessentials.listeners.SellGuiListener(), this);
        getServer().getPluginManager().registerEvents(new ShopSubGui(), this);


        // Config and Data
        ConfigManager.init(this, getDataFolder().toPath());
        BalanceManager.init(this);
        TokenManager.load();
        ShopManager.init(getDataFolder());
        HomeManager.loadHomes();
        loadBounties();


        // Tasks
        Bukkit.getScheduler().runTaskTimer(this, TaskScheduler::tickSafe, 1L, 1L);

        // Commands
        getCommand("ah").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player player)) return true;

            if (args.length == 0) {
                new AuctionGuiManager(this, auctionManager).openMainGui(player, 0);
                return true;
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("sell")) {
                return new AhSellCommand(auctionManager).onCommand(sender, command, label, new String[]{args[1]});
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("my")) {
                new MyListingsGuiManager(this, auctionManager).open(player);
                return true;
            }

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new KappaPlaceholders().register();
                getLogger().info("PlaceholderAPI expansion registered.");
            } else {
                getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
            }

            player.sendMessage(ChatColor.RED + "Usage: /ah, /ah sell <price>, or /ah my");
            return true;
        });
        registerCommand("bal", new BalCommand());
        registerCommand("pay", new PayCommand());
        registerCommand("sell", new SellCommand());
        getCommand("reload").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "Only server operators can use this command.");
                return true;
            }

            player.sendMessage(ChatColor.GREEN + "KappaEssentials reloaded successfully.");
            return true;
        });
        AfkCommand afkCommand = new AfkCommand(this);
        getCommand("afk").setExecutor(afkCommand);
        getServer().getPluginManager().registerEvents(new AfkListener(afkCommand), this);
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
        ModUpdateChecker.checkForUpdates("1.3-SNAPSHOT");

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
