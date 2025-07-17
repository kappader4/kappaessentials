package net.kappasmp.kappaessentials.bounty;

import net.kappasmp.kappaessentials.economy.BalanceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class BountyRewardHandler implements Listener {

    public static void register() {
        // Folia-safe: plugin.getServer().getPluginManager() is safe
        Bukkit.getPluginManager().registerEvents(new BountyRewardHandler(), Bukkit.getPluginManager().getPlugin("KappaEssentials"));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        UUID victimId = victim.getUniqueId();
        UUID killerId = killer.getUniqueId();

        int bounty = BountyManager.getBounty(victimId);
        if (bounty <= 0) return;

        // Reward killer
        BalanceManager.addBalance(killerId, bounty);
        killer.sendMessage(ChatColor.GREEN + "You claimed a bounty of " + ChatColor.RED + "$" + formatMoney(bounty)
                + ChatColor.GREEN + " for killing " + ChatColor.YELLOW + victim.getName());

        // Notify victim
        victim.sendMessage(ChatColor.RED + "You were killed by " + ChatColor.YELLOW + killer.getName()
                + ChatColor.RED + " and your bounty of " + ChatColor.RED + "$" + formatMoney(bounty)
                + ChatColor.RED + " was claimed.");

        // Deduct bounty amount from victim's balance
        BalanceManager.subtractBalance(victimId, bounty);

        // Remove bounty and save
        BountyManager.removeBounty(victimId);
    }

    // Format bounty amounts
    public static String formatMoney(int amount) {
        if (amount >= 1_000_000_000) return (amount / 1_000_000_000) + "B";
        if (amount >= 1_000_000) return (amount / 1_000_000) + "M";
        if (amount >= 1_000) return (amount / 1_000) + "K";
        return String.valueOf(amount);
    }
}