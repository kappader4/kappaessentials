package net.kappasmp.kappaessentials.economy;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.UUID;

public class BalCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("bal")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    return showBalance(source, source.getPlayer().getUuid(), source.getName());
                })
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            ServerCommandSource source = context.getSource();

                            ServerPlayerEntity onlinePlayer = source.getServer().getPlayerManager().getPlayer(playerName);
                            if (onlinePlayer != null) {
                                return showBalance(source, onlinePlayer.getUuid(), onlinePlayer.getName().getString());
                            } else {
                                Optional<GameProfile> profile = source.getServer().getUserCache().findByName(playerName);
                                if (profile.isPresent()) {
                                    UUID uuid = profile.get().getId();
                                    return showBalance(source, uuid, playerName);
                                } else {
                                    source.sendFeedback(() -> Text.literal("Player not found."), false);
                                    return 0;
                                }
                            }
                        })
                )
        );
    }

    private static int showBalance(ServerCommandSource source, UUID uuid, String name) {
        int balance = BalanceManager.getBalance(uuid);
        String formattedBalance = formatBalance(balance);

        MutableText message = Text.literal("Balance of ")
                .setStyle(Style.EMPTY.withColor(0xBEBEBE))
                .append(Text.literal(name).setStyle(Style.EMPTY.withColor(0xFF8300)))
                .append(Text.literal(" is ").setStyle(Style.EMPTY.withColor(0xBEBEBE)))
                .append(Text.literal("$" + formattedBalance).setStyle(Style.EMPTY.withColor(0xFF8300)));

        source.sendFeedback(() -> message, false);
        return 1;
    }

    private static String formatBalance(int balance) {
        if (balance >= 1_000_000_000) {
            return String.format("%.1fB", balance / 1_000_000_000.0);
        } else if (balance >= 1_000_000) {
            return String.format("%.1fM", balance / 1_000_000.0);
        } else if (balance >= 1_000) {
            return String.format("%.1fK", balance / 1_000.0);
        } else {
            return String.format("%d", balance);
        }
    }
}
