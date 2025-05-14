package net.kappasmp.kappaessentials.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class TaskScheduler {

    private static final Map<UUID, ScheduledTeleport> activeTeleports = new HashMap<>();
    private static final List<ScheduledMessage> scheduledMessages = new LinkedList<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Handle teleport tasks
            Iterator<Map.Entry<UUID, ScheduledTeleport>> teleportIterator = activeTeleports.entrySet().iterator();
            while (teleportIterator.hasNext()) {
                Map.Entry<UUID, ScheduledTeleport> entry = teleportIterator.next();
                ScheduledTeleport task = entry.getValue();
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());

                if (player == null || player.isDisconnected()) {
                    teleportIterator.remove();
                    continue;
                }

                if (!player.getBlockPos().equals(task.startPos)) {
                    player.sendMessage(task.cancelMessage, false);
                    teleportIterator.remove();
                    continue;
                }

                task.delay--;
                if (task.delay <= 0) {
                    task.action.run();
                    teleportIterator.remove();
                }
            }

            // Handle scheduled chat messages
            Iterator<ScheduledMessage> messageIterator = scheduledMessages.iterator();
            while (messageIterator.hasNext()) {
                ScheduledMessage message = messageIterator.next();
                message.delay--;
                if (message.delay <= 0) {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(message.playerId);
                    if (player != null && !player.isDisconnected()) {
                        message.action.run();
                    }
                    messageIterator.remove();
                }
            }
        });
    }

    public static void scheduleTeleport(ServerPlayerEntity player, int delayTicks, Runnable action, String homeName) {
        BlockPos currentPos = player.getBlockPos();
        activeTeleports.put(player.getUuid(), new ScheduledTeleport(
                delayTicks,
                action,
                currentPos,
                Text.literal("§cTeleport to §e" + homeName + "§c cancelled: you moved.")
        ));
    }

    public static void scheduleMessage(ServerPlayerEntity player, int delayTicks, Runnable action) {
        scheduledMessages.add(new ScheduledMessage(player.getUuid(), delayTicks, action));
    }

    private static class ScheduledTeleport {
        int delay;
        Runnable action;
        BlockPos startPos;
        Text cancelMessage;

        ScheduledTeleport(int delay, Runnable action, BlockPos startPos, Text cancelMessage) {
            this.delay = delay;
            this.action = action;
            this.startPos = startPos;
            this.cancelMessage = cancelMessage;
        }
    }

    private static class ScheduledMessage {
        UUID playerId;
        int delay;
        Runnable action;

        ScheduledMessage(UUID playerId, int delay, Runnable action) {
            this.playerId = playerId;
            this.delay = delay;
            this.action = action;
        }
    }
}
