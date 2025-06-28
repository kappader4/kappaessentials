package net.kappasmp.kappaessentials.command;

import com.mojang.brigadier.CommandDispatcher;
import net.kappasmp.kappaessentials.token.TokenManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class AfkCommand {
    private static final Map<UUID, Long> afkStartTimes = new HashMap<>();
    private static final Map<UUID, BlockPos> afkStartPositions = new HashMap<>();
    private static final Map<UUID, Timer> afkTimers = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("afk")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    UUID uuid = player.getUuid();

                    if (afkTimers.containsKey(uuid)) {
                        player.sendMessage(Text.literal("§cYou're already AFK! Use §e/afk stop §cto cancel it."), false);
                        return 0;
                    }

                    BlockPos startPos = player.getBlockPos();
                    afkStartTimes.put(uuid, System.currentTimeMillis());
                    afkStartPositions.put(uuid, startPos);

                    player.sendMessage(Text.literal("§6You are now AFK. §7Stay still for §61 minute§7 to earn a token."), false);

                    Timer timer = new Timer();
                    afkTimers.put(uuid, timer);

                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            ServerPlayerEntity stillPlayer = player.getServer().getPlayerManager().getPlayer(uuid);
                            if (stillPlayer == null) return;

                            BlockPos currentPos = stillPlayer.getBlockPos();
                            BlockPos originalPos = afkStartPositions.get(uuid);

                            if (currentPos.equals(originalPos)) {
                                TokenManager.giveTokens(uuid, 1);
                                stillPlayer.sendMessage(Text.literal("§6+1 Token! §7Thanks for being AFK."), false);
                            } else {
                                stillPlayer.sendMessage(Text.literal("§cYou moved! §7No token for you."), false);
                                cancelAfk(uuid);
                            }
                        }
                    }, 60_000, 60_000);

                    return 1;
                })
                .then(CommandManager.literal("stop")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            UUID uuid = player.getUuid();

                            if (!afkTimers.containsKey(uuid)) {
                                player.sendMessage(Text.literal("§cYou're not AFK right now."), false);
                                return 0;
                            }

                            cancelAfk(uuid);
                            player.sendMessage(Text.literal("§7You are no longer AFK."), false);
                            return 1;
                        }))
        );
    }

    private static void cancelAfk(UUID uuid) {
        Timer timer = afkTimers.remove(uuid);
        if (timer != null) {
            timer.cancel();
        }
        afkStartTimes.remove(uuid);
        afkStartPositions.remove(uuid);
    }
}
