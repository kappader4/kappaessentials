package net.kappasmp.kappaessentials.teleport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

                    ServerWorld targetWorld = (ServerWorld) (
                            type == TeleportRequestManager.Type.TPA ? receiver.getWorld() : requester.getWorld()
                    );

                    double x = type == TeleportRequestManager.Type.TPA ? receiver.getX() : requester.getX();
                    double y = type == TeleportRequestManager.Type.TPA ? receiver.getY() : requester.getY();
                    double z = type == TeleportRequestManager.Type.TPA ? receiver.getZ() : requester.getZ();
                    float yaw = type == TeleportRequestManager.Type.TPA ? requester.getYaw() : receiver.getYaw();
                    float pitch = type == TeleportRequestManager.Type.TPA ? requester.getPitch() : receiver.getPitch();

                    if (type == TeleportRequestManager.Type.TPA) {
                        requester.teleport(targetWorld, x, y, z, EnumSet.noneOf(PositionFlag.class), yaw, pitch);
                        requester.sendMessage(Text.literal("§aTeleported to " + receiver.getName().getString()), false);
                    } else {
                        receiver.teleport(targetWorld, x, y, z, EnumSet.noneOf(PositionFlag.class), yaw, pitch);
                        receiver.sendMessage(Text.literal("§aTeleported to " + requester.getName().getString()), false);
                    }

                    TeleportRequestManager.clearRequest(receiver);
                    return 1;
                })
        );
    }
}
