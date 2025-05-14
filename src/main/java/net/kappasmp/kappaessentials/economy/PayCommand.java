package net.kappasmp.kappaessentials.economy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PayCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("pay")
                .then(CommandManager.argument("player", StringArgumentType.string())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity sender = source.getPlayer();
                                    String targetName = StringArgumentType.getString(context, "player");
                                    int amount = IntegerArgumentType.getInteger(context, "amount");

                                    ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(targetName);
                                    if (target == null) {
                                        source.sendFeedback(() -> Text.literal("§cPlayer not found."), false);
                                        return 0;
                                    }

                                    if (sender.getUuid().equals(target.getUuid())) {
                                        source.sendFeedback(() -> Text.literal("§cYou can't pay yourself!"), false);
                                        return 0;
                                    }

                                    int senderBalance = BalanceManager.getBalance(sender.getUuid());
                                    if (senderBalance < amount) {
                                        source.sendFeedback(() -> Text.literal("§cYou don't have enough money!"), false);
                                        return 0;
                                    }

                                    // Perform the transfer
                                    BalanceManager.addBalance(sender.getUuid(), -amount);
                                    BalanceManager.addBalance(target.getUuid(), amount);

                                    source.sendFeedback(() -> Text.literal("§aYou paid §e" + target.getName().getString() + " §a$" + amount), false);
                                    target.sendMessage(Text.literal("§aYou received §a$" + amount + " §afrom §e" + sender.getName().getString()));

                                    return 1;
                                }))));
    }
}
