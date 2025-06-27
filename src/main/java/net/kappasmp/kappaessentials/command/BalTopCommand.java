package net.kappasmp.kappaessentials.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BalTopCommand {

    public static void register(com.mojang.brigadier.CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("baltop")
                .requires(source -> source.hasPermissionLevel(0))
                .executes(context -> execute(context.getSource()))
        );
    }

    private static int execute(ServerCommandSource source) {
        var topBalances = BalanceManager.getTopBalances(10);
        source.sendMessage(Text.literal("§6§lTop 10 Balances:"));
        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : topBalances) {
            String name = getPlayerName(source, entry.getKey());
            String formatted = formatBalance(entry.getValue());
            source.sendMessage(Text.literal("§6§l" + rank + ". §7" + name + " - $" + formatted));
            rank++;
        }
        return Command.SINGLE_SUCCESS;
    }

    private static String formatBalance(int balance) {
        double value = balance;
        if (value >= 1_000_000_000) {
            return String.format("%.2fb", value / 1_000_000_000);
        } else if (value >= 1_000_000) {
            return String.format("%.2fm", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.2fk", value / 1_000);
        } else {
            return String.format("%.2f", value);
        }
    }

    private static String getPlayerName(ServerCommandSource source, UUID uuid) {
        var player = source.getServer().getPlayerManager().getPlayer(uuid);
        if (player != null) {
            return player.getName().getString();
        } else {
            Optional<com.mojang.authlib.GameProfile> profile = source.getServer().getUserCache().getByUuid(uuid);
            return profile.map(com.mojang.authlib.GameProfile::getName).orElse(uuid.toString().substring(0, 8));
        }
    }
}
