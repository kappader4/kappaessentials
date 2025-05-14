package net.kappasmp.kappaessentials.bounty;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.kappasmp.kappaessentials.gui.BountyGui;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class BountyCommand {

    public static void register(com.mojang.brigadier.CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("bounty")
                .requires(source -> source.hasPermissionLevel(0))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();

                    if (source.getPlayer() instanceof ServerPlayerEntity player) {
                        new BountyGui(player).open();
                        return 1;
                    } else {
                        source.sendMessage(Text.literal("Only players can use this command."));
                        return 0;
                    }
                })
        );
    }
}
