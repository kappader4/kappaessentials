package net.kappasmp.kappaessentials.bounty;

import net.kappasmp.kappaessentials.economy.BalanceManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class BountyRewardHandler implements Listener {

    public static String formatMoney(int amount) {
        if (amount >= 1_000_000_000) {
            return (amount / 1_000_000_000) + "B";
        } else if (amount >= 1_000_000) {
            return (amount / 1_000_000) + "M";
        } else if (amount >= 1_000) {
            return (amount / 1_000) + "K";
        }
        return String.valueOf(amount);
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        Player killer = victim.getKiller();
        if (killer == null) return;

        int bounty = BountyManager.getBounty(victim.getUniqueId());
        if (bounty <= 0) return;

        String formattedBounty = formatMoney(bounty);

        // Give bounty reward
        BalanceManager.addBalance(killer.getUniqueId(), bounty);
        killer.sendMessage(ChatColor.GREEN + "You claimed a bounty of " + ChatColor.RED + "$" + formattedBounty +
                ChatColor.GREEN + " for killing " + ChatColor.YELLOW + victim.getName());

        // Notify victim
        victim.sendMessage(ChatColor.RED + "You were killed by " + ChatColor.YELLOW + killer.getName() +
                ChatColor.RED + " and your bounty of " + ChatColor.RED + "$" + formattedBounty + ChatColor.RED + " was claimed.");

        // Deduct bounty from victim's balance
        BalanceManager.subtractBalance(victim.getUniqueId(), bounty);

        // Remove and save
        BountyManager.removeBounty(victim.getUniqueId());
    }
}
