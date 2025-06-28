package net.kappasmp.kappaessentials.economy;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

public class SellCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sell")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ItemStack heldItem = player.getMainHandStack();

                    // If the player isn't holding anything
                    if (heldItem.isEmpty()) {
                        player.sendMessage(
                                Text.literal("You're not holding any item to sell.")
                                        .setStyle(Style.EMPTY.withColor(0xBEBEBE)),
                                false
                        );
                        return 0;
                    }

                    String itemId = Registries.ITEM.getId(heldItem.getItem()).toString();
                    int count = heldItem.getCount();
                    int pricePerItem = BalanceManager.getItemPrice(itemId);

                    // If the item can't be sold
                    if (pricePerItem <= 0) {
                        player.sendMessage(
                                Text.literal("You cannot sell this item.")
                                        .setStyle(Style.EMPTY.withColor(0xBEBEBE)),
                                false
                        );
                        return 0;
                    }

                    int totalValue = pricePerItem * count;
                    heldItem.decrement(count);
                    BalanceManager.addBalance(player.getUuid(), totalValue);

                    // Create feedback message
                    MutableText message = Text.literal("Sold ")
                            .setStyle(Style.EMPTY.withColor(0xBEBEBE))
                            .append(Text.literal(count + "x ")
                                    .setStyle(Style.EMPTY.withColor(0xFF8300)))
                            .append(Text.literal(itemId.split(":")[1] + " ")
                                    .setStyle(Style.EMPTY.withColor(0xBEBEBE)))
                            .append(Text.literal("for ")
                                    .setStyle(Style.EMPTY.withColor(0xBEBEBE)))
                            .append(Text.literal("$" + totalValue)
                                    .setStyle(Style.EMPTY.withColor(0xFF8300)));

                    player.sendMessage(message, false);
                    return 1;
                })
                // New subcommand for selling the inventory
                .then(CommandManager.literal("inventory")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            int totalValue = 0;

                            // Loop through player's inventory
                            for (int i = 0; i < player.getInventory().size(); i++) {
                                ItemStack stack = player.getInventory().getStack(i);

                                if (!stack.isEmpty()) {
                                    String itemId = Registries.ITEM.getId(stack.getItem()).toString();
                                    int pricePerItem = BalanceManager.getItemPrice(itemId);

                                    // If the item can't be sold, skip it
                                    if (pricePerItem <= 0) {
                                        continue;
                                    }

                                    int itemTotalValue = pricePerItem * stack.getCount();
                                    totalValue += itemTotalValue;
                                    stack.decrement(stack.getCount());  // Decrease the item count in the inventory
                                }
                            }

                            if (totalValue > 0) {
                                BalanceManager.addBalance(player.getUuid(), totalValue);

                                MutableText message = Text.literal("Sold all items in your inventory for ")
                                        .setStyle(Style.EMPTY.withColor(0xBEBEBE))
                                        .append(Text.literal("$" + totalValue)
                                                .setStyle(Style.EMPTY.withColor(0xFF8300)));

                                player.sendMessage(message, false);
                            } else {
                                player.sendMessage(
                                        Text.literal("You don't have any sellable items in your inventory.")
                                                .setStyle(Style.EMPTY.withColor(0xBEBEBE)),
                                        false
                                );
                            }

                            return 1;
                        })
                )
        );
    }
}
