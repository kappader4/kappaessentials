package net.kappasmp.kappaessentials.teleport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.EnumSet;

public class TpAcceptCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tpaccept")
                .executes(context -> {
                    ServerPlayerEntity receiver = context.getSource().getPlayer();

                    if (!TeleportRequestManager.hasRequest(receiver)) {
                        receiver.sendMessage(Text.literal("§cNo pending teleport requests."), false);
                        return 0;
                    }

                    ServerPlayerEntity requester = TeleportRequestManager.getRequester(receiver);
                    TeleportRequestManager.Type type = TeleportRequestManager.getType(receiver);

                    if (requester == null || type == null) {
                        receiver.sendMessage(Text.literal("§cTeleport request is no longer valid."), false);
                        return 0;
                    }

                    if (type == TeleportRequestManager.Type.TPA) {
                        requester.teleport(
                                receiver.getServerWorld(),
                                receiver.getX(), receiver.getY(), receiver.getZ(),
                                EnumSet.noneOf(PositionFlag.class),
                                requester.getYaw(), requester.getPitch(),
                                false
                        );
                        requester.sendMessage(Text.literal("§aTeleported to " + receiver.getName().getString()), false);
                    } else {
                        receiver.teleport(
                                requester.getServerWorld(),
                                requester.getX(), requester.getY(), requester.getZ(),
                                EnumSet.noneOf(PositionFlag.class),
                                receiver.getYaw(), receiver.getPitch(),
                                false
                        );
                        receiver.sendMessage(Text.literal("§aTeleported to " + requester.getName().getString()), false);
                    }

                    TeleportRequestManager.clearRequest(receiver);
                    return 1;
                })
        );
    }
}
