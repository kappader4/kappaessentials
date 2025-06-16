package net.kappasmp.kappaessentials.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.kappasmp.kappaessentials.token.TokenManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class KappaPlaceholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "kappaessentials";
    }

    @Override
    public @NotNull String getAuthor() {
        return "KappaDev";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    // Do not use @Override — this is not in PlaceholderExpansion superclass
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null || player.getUniqueId() == null) return "";

        UUID uuid = player.getUniqueId();

        return switch (identifier.toLowerCase()) {
            case "tokens" -> String.valueOf(TokenManager.getTokens(uuid));
            case "bal", "balance" -> formatBalance(BalanceManager.getBalance(uuid));
            default -> null;
        };
    }

    private String formatBalance(int amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB", amount / 1_000_000_000.0);
        if (amount >= 1_000_000) return String.format("%.1fM", amount / 1_000_000.0);
        if (amount >= 1_000) return String.format("%.1fK", amount / 1_000.0);
        return String.valueOf(amount);
    }
}
