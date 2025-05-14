package net.kappasmp.kappaessentials.token;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TokensCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tokens")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    int tokens = TokenManager.getTokens(player.getUuid());
                    player.sendMessage(Text.literal("§7Your token balance: §6" + tokens), false);
                    return 1;
                }));
    }
}
