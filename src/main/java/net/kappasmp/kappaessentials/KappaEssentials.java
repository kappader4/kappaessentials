package net.kappasmp.kappaessentials;

import net.kappasmp.kappaessentials.bounty.BountyCommand;
import net.kappasmp.kappaessentials.bounty.BountyManager;
import net.kappasmp.kappaessentials.command.*;
import net.kappasmp.kappaessentials.config.ConfigManager;
import net.kappasmp.kappaessentials.economy.*;
import net.kappasmp.kappaessentials.gui.RtpGuiListener;
import net.kappasmp.kappaessentials.gui.ShopGui;
import net.kappasmp.kappaessentials.gui.ShopGuiListener;
import net.kappasmp.kappaessentials.gui.ShopSubGuiListener;
import net.kappasmp.kappaessentials.homes.HomeCommand;
import net.kappasmp.kappaessentials.homes.HomeManager;
import net.kappasmp.kappaessentials.*;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.placeholder.KappaPlaceholders;
import net.kappasmp.kappaessentials.teleport.*;
import net.kappasmp.kappaessentials.token.*;
import net.kappasmp.kappaessentials.update.ModUpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public class KappaEssentials extends JavaPlugin implements Listener {

	public static final String MOD_ID = "kappaessentials";

	@Override
	public void onEnable() {
		getLogger().info("KappaEssentials enabled");

		HomeManager.init(getDataFolder());

		BalanceManager.init(this);

		ConfigManager.load();                      // changed from getDataFolder().toPath()
		BalanceManager.loadBalances();
		BalanceManager.loadItemPrices();
		TokenManager.init(getDataFolder());
		BountyManager.loadBounties();
		ShopManager.init(this);
		HomeManager.loadHomes();
		ShopManager.load();
		TokenManager.load();

		getServer().getPluginManager().registerEvents(new RtpGuiListener(), this);

		getServer().getPluginManager().registerEvents(new ShopGuiListener(), this);
		getServer().getPluginManager().registerEvents(new ShopSubGuiListener(), this);

		registerCommands();
		Bukkit.getPluginManager().registerEvents(this, this);
		ModUpdateChecker.checkForUpdates(getDescription().getVersion(), this);

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new KappaPlaceholders().register();
			getLogger().info("PlaceholderAPI expansion registered.");
		} else {
			getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
		}
	}

	@Override
	public void onDisable() {
		BalanceManager.saveBalances();
		BalanceManager.saveItemPrices();
		TokenManager.save();
		HomeManager.saveHomes();
	}

	private void registerCommands() {
		getCommand("bal").setExecutor(new BalCommand());
		getCommand("sell").setExecutor(new SellCommand());
		getCommand("reload").setExecutor(new ReloadCommand());
		getCommand("afk").setExecutor(new AfkCommand());
		getCommand("tokens").setExecutor(new TokensCommand());
		getCommand("tokentop").setExecutor(new TokenTopCommand());
		getCommand("baltop").setExecutor(new BalTopCommand());
		getCommand("pay").setExecutor(new PayCommand());
		getCommand("bounty").setExecutor(new BountyCommand());
		getCommand("rtpgui").setExecutor(new RtpGuiCommand());

		PluginCommand shopCommand = getCommand("shop");
		if (shopCommand != null) {
			shopCommand.setExecutor((sender, command, label, args) -> {
				if (!(sender instanceof Player player)) {
					sender.sendMessage("This command can only be used by players.");
					return true;
				}
				ShopGui.open(player);  // changed from `new ShopGui(player).open();`
				return true;
			});
		}
	}

	public static void onPlayerLeave(Player player) {
		BalanceManager.savePlayerBalance(player);
	}

	public static void log(String message) {
		Bukkit.getLogger().info("[" + MOD_ID + "] " + message);
	}

	public static String formatBalance(double balance) {
		if (balance >= 1_000_000_000) {
			return String.format("%.2fB", balance / 1_000_000_000.0);
		} else if (balance >= 1_000_000) {
			return String.format("%.2fM", balance / 1_000_000.0);
		} else if (balance >= 1_000) {
			return String.format("%.2fK", balance / 1_000.0);
		} else {
			return String.format("%.2f", balance);
		}
	}

	public static String formatTokens(int tokens) {
		double value = tokens;
		if (value >= 1_000_000_000) {
			return String.format("%.2fb", value / 1_000_000_000);
		} else if (value >= 1_000_000) {
			return String.format("%.2fm", value / 1_000_000);
		} else if (value >= 1_000) {
			return String.format("%.2fk", value / 1_000);
		} else {
			return String.format("%.0f", value);
		}
	}

	public static String colored(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static UUID getUUID(Player player) {
		return player.getUniqueId();
	}
}