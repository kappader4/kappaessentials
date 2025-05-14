package net.kappasmp.kappaessentials.homes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.kappasmp.kappaessentials.homes.HomeManager.HomeData;
import net.kappasmp.kappaessentials.util.TaskScheduler;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HomeCommand {

    private static final int TELEPORT_DELAY_SECONDS = 5;

    private static final SuggestionProvider<ServerCommandSource> HOME_NAME_SUGGESTIONS = (context, builder) -> {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Map<String, HomeData> homes = HomeManager.getHomes(player);
        homes.keySet().forEach(builder::suggest);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        // /home <homeName>
        dispatcher.register(CommandManager.literal("home")
                .then(CommandManager.argument("homeName", StringArgumentType.string())
                        .suggests(HOME_NAME_SUGGESTIONS)
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            MinecraftServer server = context.getSource().getServer();
                            String homeName = StringArgumentType.getString(context, "homeName");

                            HomeData home = HomeManager.getHome(player, homeName);
                            if (home == null) {
                                player.sendMessage(Text.literal("§cHome not found: " + homeName), false);
                                return 1;
                            }

                            ServerWorld targetWorld = server.getWorld(home.dimension);
                            if (targetWorld == null) {
                                player.sendMessage(Text.literal("§cWorld not found: " + home.dimension.getValue()), false);
                                return 1;
                            }

                            BlockPos pos = home.pos;
                            ChunkPos chunkPos = new ChunkPos(pos);

                            // Send countdown messages (safe even if player moves)
                            for (int second = 1; second <= TELEPORT_DELAY_SECONDS; second++) {
                                int timeLeft = TELEPORT_DELAY_SECONDS - second + 1;
                                int delayTicks = second * 20;

                                TaskScheduler.scheduleMessage(player, delayTicks, () ->
                                        player.sendMessage(Text.literal("§7Teleporting in §e" + timeLeft + "§7..."), false)
                                );
                            }

                            // Schedule teleport after delay (cancel if player moves)
                            TaskScheduler.scheduleTeleport(player, TELEPORT_DELAY_SECONDS * 20, () -> {
                                CompletableFuture.runAsync(() -> {
                                    targetWorld.getChunk(chunkPos.x, chunkPos.z);
                                }, server).thenRunAsync(() -> {
                                    teleportSafely(player, targetWorld, pos);
                                    player.sendMessage(Text.literal("§aTeleported to home: " + homeName), false);
                                }, server);
                            }, homeName);

                            return 1;
                        })
                )
        );

        // /sethome <homeName>
        dispatcher.register(CommandManager.literal("sethome")
                .then(CommandManager.argument("homeName", StringArgumentType.string())
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            String homeName = StringArgumentType.getString(context, "homeName");

                            if (HomeManager.getHome(player, homeName) != null) {
                                player.sendMessage(Text.literal("§cHome already exists: " + homeName), false);
                                return 1;
                            }

                            RegistryKey<World> worldKey = player.getWorld().getRegistryKey();
                            BlockPos pos = player.getBlockPos();
                            HomeData home = new HomeData(pos, worldKey);
                            HomeManager.setHome(player, homeName, home);

                            player.sendMessage(Text.literal("§aHome '" + homeName + "' has been set."), false);
                            return 1;
                        })
                )
        );

        // /delhome <homeName>
        dispatcher.register(CommandManager.literal("delhome")
                .then(CommandManager.argument("homeName", StringArgumentType.string())
                        .suggests(HOME_NAME_SUGGESTIONS)
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            String homeName = StringArgumentType.getString(context, "homeName");

                            Map<String, HomeData> homes = HomeManager.getHomes(player);
                            if (!homes.containsKey(homeName)) {
                                player.sendMessage(Text.literal("§cHome not found: " + homeName), false);
                                return 1;
                            }

                            HomeManager.removeHome(player, homeName);
                            player.sendMessage(Text.literal("§aHome '" + homeName + "' has been deleted."), false);
                            return 1;
                        })
                )
        );
    }

    /**
     * Teleports the player safely across dimensions.
     */
    private static void teleportSafely(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        if (player.getServerWorld() != world) {
            player.teleport(
                    world, x, y, z,
                    EnumSet.noneOf(PositionFlag.class),
                    player.getYaw(), player.getPitch(),
                    false
            );
        } else {
            player.networkHandler.requestTeleport(
                    x, y, z,
                    player.getYaw(), player.getPitch()
            );
        }
    }
}
