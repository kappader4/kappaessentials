package net.kappasmp.kappaessentials.teleport;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.network.packet.s2c.play.PositionFlag;

import java.util.*;

public class HomeTeleportScheduler {
    private static final List<TeleportTask> tasks = new ArrayList<>();

    public static void tick(MinecraftServer server) {
        Iterator<TeleportTask> iterator = tasks.iterator();

        while (iterator.hasNext()) {
            TeleportTask task = iterator.next();

            if (!task.player.getBlockPos().equals(task.startPos)) {
                task.player.sendMessage(Text.literal("§cYou moved! Teleportation cancelled."), false);
                iterator.remove();
                continue;
            }

            if (task.delayTicks == 0) {
                ServerWorld targetWorld = server.getWorld(task.dimension);
                if (targetWorld == null) {
                    task.player.sendMessage(Text.literal("§cError: world not found!"), false);
                    iterator.remove();
                    continue;
                }

                task.player.teleport(
                        targetWorld,
                        task.pos.getX() + 0.5,
                        task.pos.getY(),
                        task.pos.getZ() + 0.5,
                        EnumSet.noneOf(PositionFlag.class),
                        task.player.getYaw(),
                        task.player.getPitch(),
                        false
                );

                task.player.sendMessage(Text.literal("§aTeleported to home '" + task.name + "'!"), false);
                iterator.remove();
                continue;
            }

            if (task.delayTicks % 20 == 0) {
                int secondsLeft = task.delayTicks / 20;
                task.player.sendMessage(Text.literal("§7Teleporting in " + secondsLeft + "..."), false);
            }

            task.delayTicks--;
        }
    }

    public static void queue(ServerPlayerEntity player, BlockPos pos, RegistryKey<World> dimension, String name) {
        tasks.add(new TeleportTask(player, pos, dimension, name));
    }

    private static class TeleportTask {
        ServerPlayerEntity player;
        BlockPos pos;
        RegistryKey<World> dimension;
        String name;
        int delayTicks = 5 * 20;
        BlockPos startPos;

        TeleportTask(ServerPlayerEntity player, BlockPos pos, RegistryKey<World> dimension, String name) {
            this.player = player;
            this.pos = pos;
            this.dimension = dimension;
            this.name = name;
            this.startPos = player.getBlockPos();
        }
    }
}
