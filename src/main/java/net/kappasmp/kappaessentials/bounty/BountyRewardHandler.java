package net.kappasmp.kappaessentials.bounty;

import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;

public class BountyRewardHandler {

    // Format the money to show K, M, B (for thousands, millions, billions)
    public static String formatMoney(int amount) {
        if (amount >= 1_000_000_000) {
            return (amount / 1_000_000_000) + "B";
        } else if (amount >= 1_000_000) {
            return (amount / 1_000_000) + "M";
        } else if (amount >= 1_000) {
            return (amount / 1_000) + "K";
        }
        return String.valueOf(amount);  // For numbers less than 1000
    }

    public static void register() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (!(entity instanceof ServerPlayerEntity killer)) return;
            if (!(killedEntity instanceof ServerPlayerEntity victim)) return;

            int bounty = BountyManager.getBounty(victim.getUuid());
            if (bounty <= 0) return;

            // Format the bounty amount for the message
            String formattedBounty = formatMoney(bounty);

            // Give bounty reward to killer
            BalanceManager.addBalance(killer.getUuid(), bounty);
            killer.sendMessage(Text.literal("§aYou claimed a bounty of §c$" + formattedBounty + " §afor killing §e" + victim.getName().getString()), false);

            // Notify victim
            victim.sendMessage(Text.literal("§cYou were killed by §e" + killer.getName().getString() + "§c and your bounty of §c$" + formattedBounty + " §cwas claimed."), false);

            // Deduct bounty from victim's balance
            BalanceManager.subtractBalance(victim.getUuid(), bounty);

            // Remove bounty after it is claimed and save the bounty data
            BountyManager.removeBounty(victim.getUuid());

            // Save bounty data to file
            BountyManager.saveBounties();  // Directly call the save method from BountyManager
        });
    }
}
