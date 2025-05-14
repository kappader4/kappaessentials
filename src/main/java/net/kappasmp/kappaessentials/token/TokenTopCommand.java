package net.kappasmp.kappaessentials.token;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TokenTopCommand {

    public static void register(com.mojang.brigadier.CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("tokentop")
                .requires(source -> source.hasPermissionLevel(0))
                .executes(context -> execute(context.getSource()))
        );
    }

    private static int execute(ServerCommandSource source) {
        var topTokens = TokenManager.getTopTokens(10);
        source.sendMessage(Text.literal("§6§lTop 10 Tokens:"));
        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : topTokens) {
            String name = getPlayerName(source, entry.getKey());
            String formatted = formatTokens(entry.getValue());
            source.sendMessage(Text.literal("§6§l" + rank + ". §7" + name + " - " + formatted + " Tokens"));
            rank++;
        }
        return Command.SINGLE_SUCCESS;
    }

    private static String formatTokens(int tokens) {
        double value = tokens;
        if (value >= 1_000_000_000) {
            return String.format("%.2fb", value / 1_000_000_000);
        } else if (value >= 1_000_000) {
            return String.format("%.2fm", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.2fk", value / 1_000);
        } else {
            return String.format("%.0f", value); // No decimals
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
