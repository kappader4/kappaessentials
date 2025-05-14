package net.kappasmp.kappaessentials.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.kappasmp.kappaessentials.manager.ShopManager;
import net.kappasmp.kappaessentials.model.ShopCategory;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ShopGui extends SimpleGui {

    public ShopGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
        this.setTitle(Text.literal("§8> ѕʜᴏᴘ"));

        for (ShopCategory category : ShopManager.getMainMenu()) {
            Identifier id = Identifier.tryParse(category.icon);
            if (id == null || !Registries.ITEM.containsId(id)) {
                System.err.println("[ShopGui] Invalid icon ID: " + category.icon);
                continue;
            }

            Item icon = Registries.ITEM.get(id);
            this.setSlot(category.slot, new GuiElementBuilder(icon)
                    .setName(Text.literal(category.name))
                    .setCallback((index, type, action, gui) -> {
                        player.sendMessage(Text.literal("Opening " + category.name + "..."), false);
                        new ShopSubGui(player, category.shopId).open();
                    }));
        }
    }
}
