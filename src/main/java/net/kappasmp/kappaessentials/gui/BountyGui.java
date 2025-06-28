package net.kappasmp.kappaessentials.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.kappasmp.kappaessentials.bounty.BountyManager;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.server.command.ServerCommandSource;

import java.text.NumberFormat;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

public class BountyGui extends SimpleGui {

    public BountyGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.setTitle(Text.literal("> ʙᴏᴜɴᴛɪᴇꜱ").formatted(Formatting.DARK_GRAY));

        int slot = 0;
        var bountiedPlayers = BountyManager.getAllBountiedPlayers();

        // Show placeholder if no bounties
        if (bountiedPlayers.isEmpty()) {
            this.setSlot(22, new GuiElementBuilder(Items.BARRIER)
                    .setName(Text.literal("No bounties yet!").formatted(Formatting.DARK_GRAY))
                    .addLoreLine(Text.literal("§7Use the anvil below to add one.")));
        } else {
            // Sort players by bounty amount (highest first)
            List<UUID> sortedBountiedPlayers = bountiedPlayers.stream()
                    .sorted((uuid1, uuid2) -> Integer.compare(BountyManager.getBounty(uuid2), BountyManager.getBounty(uuid1)))
                    .collect(Collectors.toList());

            // Loop through sorted bountied players
            for (UUID targetUUID : sortedBountiedPlayers) {
                String name = getPlayerName(player.getServer().getCommandSource(), targetUUID);
                int amount = BountyManager.getBounty(targetUUID);

                // Format the bounty amount
                String formattedAmount = formatMoney(amount);

                // Set player head and bounty info
                this.setSlot(slot++, new GuiElementBuilder(Items.PLAYER_HEAD)
                        .setSkullOwner(targetUUID.toString())  // Fetch the correct player head by UUID
                        .setName(Text.literal("☠ " + name).formatted(Formatting.GOLD))
                        .addLoreLine(Text.literal("§7ʙᴏᴜɴᴛʏ: §c" + formattedAmount))
                        .setCallback((i, t, a, gui) -> {
                            player.sendMessage(Text.literal("ᴛᴀʀɢᴇᴛ: " + name + " ʜᴀѕ ᴀ $" + formattedAmount + " bounty."), false);
                        }));
            }
        }

        // Button to set new bounty
        this.setSlot(49, new GuiElementBuilder(Items.ANVIL)
                .setName(Text.literal("➕ ѕᴇᴛ ɴᴇᴡ ʙᴏᴜɴᴛɪᴇ").formatted(Formatting.GREEN, Formatting.BOLD))
                .addLoreLine(Text.literal("ᴄʟɪᴄᴋ ᴛᴏ ѕᴇᴛ ᴀ ʙᴏᴜɴᴛɪᴇ ᴏɴ ᴀ ᴘʟᴀʏᴇʀ."))
                .setCallback((i, t, a, gui) -> {
                    player.closeHandledScreen();
                    new SelectBountyTargetGui(player).open();
                }));
    }

    // Money formatting method (K = thousand, M = million, B = billion)
    public static String formatMoney(int amount) {
        if (amount >= 1_000_000_000) {
            return (amount / 1_000_000_000) + "B";
        } else if (amount >= 1_000_000) {
            return (amount / 1_000_000) + "M";
        } else if (amount >= 1_000) {
            return (amount / 1_000) + "K";
        }
        return String.valueOf(amount);  // For numbers less than 1000
    }

    // Method to get the player name, considering both online and offline players
    private static String getPlayerName(ServerCommandSource source, UUID uuid) {
        var player = source.getServer().getPlayerManager().getPlayer(uuid);
        if (player != null) {
            return player.getName().getString();
        } else {
            Optional<com.mojang.authlib.GameProfile> profile = source.getServer().getUserCache().getByUuid(uuid);
            return profile.map(com.mojang.authlib.GameProfile::getName).orElse(uuid.toString().substring(0, 8));
        }
    }

    // GUI to choose which player to place a bounty on
    public static class SelectBountyTargetGui extends SimpleGui {

        public SelectBountyTargetGui(ServerPlayerEntity player) {
            super(ScreenHandlerType.GENERIC_9X6, player, false);
            this.setTitle(Text.literal("> ѕᴇʟᴇᴄᴛ ᴛᴀʀɢᴇᴛ").formatted(Formatting.DARK_GRAY));

            int slot = 0;
            for (ServerPlayerEntity target : player.getServer().getPlayerManager().getPlayerList()) {
                if (target.getUuid().equals(player.getUuid())) continue;

                int amount = BountyManager.getBounty(target.getUuid());
                String formattedAmount = NumberFormat.getInstance().format(amount);

                this.setSlot(slot++, new GuiElementBuilder(Items.PLAYER_HEAD)
                        .setSkullOwner(target.getName().getString()) // Use name string
                        .setName(Text.literal("☠ " + target.getName().getString()).formatted(Formatting.GOLD))
                        .addLoreLine(Text.literal("§7Bounty: §c$" + formattedAmount))
                        .setCallback((i, t, a, gui) -> {
                            player.sendMessage(Text.literal("Target: " + target.getName().getString() + " has a $" + formattedAmount + " bounty."), false);
                            player.closeHandledScreen();
                            openAmountInput(player, target);
                        }));
            }
        }
        private void openAmountInput(ServerPlayerEntity source, ServerPlayerEntity target) {
            AnvilInputGui inputGui = new AnvilInputGui(source, false);

            inputGui.setSlot(1, new GuiElementBuilder(Items.PAPER)
                    .setName(Text.literal("§eᴇɴᴛᴇʀ ʙᴏᴜɴᴛɪᴇꜱ ᴀᴍᴏᴜɴᴛ"))
                    .addLoreLine(Text.literal("§7Type an amount and click here."))
                    .setCallback((index, type, action, gui) -> {
                        String text = inputGui.getInput();
                        try {
                            int amount = Integer.parseInt(text);
                            if (amount <= 0) throw new NumberFormatException();

                            if (BalanceManager.getBalance(source.getUuid()) < amount) {
                                source.sendMessage(Text.literal("§cYou don't have enough balance to set this bounty."));
                                return;
                            }

                            //  Updated to include the source (setter)
                            BountyManager.setBounty(target.getUuid(), source.getUuid(), amount);

                            // Deduct balance
                            BalanceManager.addBalance(source.getUuid(), -amount);

                            source.sendMessage(Text.literal("§aʙᴏᴜɴᴛʏ ѕᴇᴛ ᴏɴ §e" + target.getName().getString() + " §afor §c$" + BountyGui.formatMoney(amount)));
                        } catch (NumberFormatException e) {
                            source.sendMessage(Text.literal("§cɪɴᴠᴀʟɪᴅ ʙᴏᴜɴᴛʏ ᴀᴍᴏᴜɴᴛ."));
                        }
                        gui.close();
                    }));

            inputGui.open();
        }
    }
}