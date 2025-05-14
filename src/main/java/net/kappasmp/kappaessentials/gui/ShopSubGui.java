package net.kappasmp.kappaessentials.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.kappasmp.kappaessentials.economy.BalanceManager;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.model.ShopItem;
import net.kappasmp.kappaessentials.model.ShopShop;
import net.kappasmp.kappaessentials.token.TokenManager;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ShopSubGui extends SimpleGui {

    public ShopSubGui(ServerPlayerEntity player, String shopId) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);

        Optional<ShopShop> optionalShop = ShopManager.getShopById(shopId);
        if (optionalShop.isEmpty()) {
            this.setTitle(Text.literal("§cShop Not Found"));
            return;
        }

        ShopShop shop = optionalShop.get();
        this.setTitle(Text.literal(shop.title));

        // Fill background
        Item filler = Registries.ITEM.get(Identifier.tryParse("minecraft:gray_stained_glass_pane"));
        for (int i = 0; i < 27; i++) {
            this.setSlot(i, new GuiElementBuilder(filler).setName(Text.literal("")));
        }

        for (ShopItem item : shop.items) {
            Identifier itemId = Identifier.tryParse(item.id);
            if (itemId == null || !Registries.ITEM.containsId(itemId)) {
                System.err.println("[Shop] Invalid item ID in config: " + item.id);
                continue;
            }

            Item mcItem = Registries.ITEM.get(itemId);

            GuiElementBuilder builder = new GuiElementBuilder(mcItem)
                    .setName(Text.literal("§f" + mcItem.getName().getString()))
                    .setLore(List.of(
                            Text.literal("§7Click to purchase"),
                            Text.literal("§6" + (shop.currency.equalsIgnoreCase("tokens") ? "Token Cost" : "💰 Cost") + ": §e" + item.price),
                            Text.literal("§6Amount: §e" + item.amount)
                    ))
                    .setCallback((index, type, action, gui) -> handlePurchase(player, shop.currency, item));

            this.setSlot(item.slot, builder);
        }
    }

    private void handlePurchase(ServerPlayerEntity player, String currency, ShopItem item) {
        UUID uuid = player.getUuid();
        boolean paid = currency.equalsIgnoreCase("tokens")
                ? TokenManager.withdrawTokens(uuid, item.price)
                : BalanceManager.withdrawBalance(uuid, item.price);

        if (!paid) {
            player.sendMessage(Text.literal("§cYou do not have enough " + (currency.equalsIgnoreCase("tokens") ? "Tokens" : "Money") + "."), false);
            return;
        }

        String command = (item.customCommand != null && !item.customCommand.isEmpty())
                ? item.customCommand.replace("%player%", player.getName().getString())
                : "give " + player.getName().getString() + " " + item.id + " " + item.amount;

        player.getServer().getCommandManager().executeWithPrefix(player.getServer().getCommandSource(), command);
        player.sendMessage(Text.literal("§aPurchase successful!"), false);
    }
}
