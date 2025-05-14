package net.kappasmp.kappaessentials.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TpahereCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tpahere")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests((context, builder) -> CommandSource.suggestMatching(context.getSource().getServer().getPlayerNames(), builder))
                        .executes(context -> {
                            ServerPlayerEntity sender = context.getSource().getPlayer();
                            String targetName = StringArgumentType.getString(context, "player");
                            MinecraftServer server = context.getSource().getServer();

                            ServerPlayerEntity target = server.getPlayerManager().getPlayer(targetName);
                            if (target == null || target == sender) {
                                sender.sendMessage(Text.literal("§cInvalid target player."), false);
                                return 0;
                            }

                            TeleportRequestManager.sendRequest(sender, target, TeleportRequestManager.Type.TPAHERE);
                            sender.sendMessage(Text.literal("§aTPAHere request sent to " + target.getName().getString()), false);
                            target.sendMessage(Text.literal("§e" + sender.getName().getString() + " wants you to teleport to them. Type §a/tpaccept §eor §c/tpdeny."), false);
                            return 1;
                        })
                )
        );
    }
}
