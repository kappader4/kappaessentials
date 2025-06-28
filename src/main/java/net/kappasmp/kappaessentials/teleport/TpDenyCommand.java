package net.kappasmp.kappaessentials.teleport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TpDenyCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tpdeny")
                .executes(context -> {
                    ServerPlayerEntity receiver = context.getSource().getPlayer();

                    if (!TeleportRequestManager.hasRequest(receiver)) {
                        receiver.sendMessage(Text.literal("§cNo pending teleport requests."), false);
                        return 0;
                    }

                    ServerPlayerEntity requester = TeleportRequestManager.getRequester(receiver);
                    if (requester != null) {
                        requester.sendMessage(Text.literal("§cYour teleport request was denied."), false);
                    }

                    receiver.sendMessage(Text.literal("§7Teleport request denied."), false);
                    TeleportRequestManager.clearRequest(receiver);
                    return 1;
                })
        );
    }
}
