package net.kappasmp.kappaessentials.model;

import org.bukkit.Material;

import java.util.*;

public class ShopModels {

    public static class ShopCategory {
        private final String name;
        private final String icon;
        private final int slot;
        private final String shopId;

        public ShopCategory(String name, String icon, int slot, String shopId) {
            this.name = name;
            this.icon = icon;
            this.slot = slot;
            this.shopId = shopId;
        }

        public static ShopCategory fromMap(Map<String, Object> map) {
            String name = Objects.toString(map.get("name"), "Unnamed");
            String icon = Objects.toString(map.get("icon"), "BARRIER");
            int slot = (int) map.getOrDefault("slot", 0);
            String shopId = Objects.toString(map.get("shopId"), "default");

            return new ShopCategory(name, icon, slot, shopId);
        }

        public String getName() { return name; }
        public String getIcon() { return icon; }
        public int getSlot() { return slot; }
        public String getShopId() { return shopId; }
    }

    public static class ShopItem {
        private final int slot;
        private final String id;
        private final int price;
        private final int amount;
        private final String customName;
        private final List<String> customLore;
        private final String customCommand;
        private final String currency;

        public ShopItem(int slot, String id, int price, int amount, String customName,
                        List<String> customLore, String customCommand, String currency) {
            this.slot = slot;
            this.id = id;
            this.price = price;
            this.amount = amount;
            this.customName = customName;
            this.customLore = customLore;
            this.customCommand = customCommand;
            this.currency = currency;
        }

        @SuppressWarnings("unchecked")
        public static ShopItem fromMap(Map<String, Object> map, String parentCurrency) {
            int slot = (int) map.getOrDefault("slot", 0);
            String id = Objects.toString(map.get("id"), "minecraft:barrier");
            int price = (int) map.getOrDefault("price", 0);
            int amount = (int) map.getOrDefault("amount", 1);
            String customName = (String) map.getOrDefault("customName", null);
            List<String> customLore = (List<String>) map.getOrDefault("customLore", new ArrayList<>());
            String customCommand = (String) map.getOrDefault("customCommand", null);
            String currency = (String) map.getOrDefault("currency", parentCurrency);

            return new ShopItem(slot, id, price, amount, customName, customLore, customCommand, currency);
        }

        public int getSlot() { return slot; }
        public String getId() { return id; }
        public int getPrice() { return price; }
        public int getAmount() { return amount; }
        public String getCustomName() { return customName; }
        public List<String> getCustomLore() { return customLore; }
        public String getCustomCommand() { return customCommand; }
        public String getCurrency() { return currency; }
    }

    public static class ShopShop {
        private final String id;
        private final String title;
        private final String currency;
        private final List<ShopItem> items;

        public ShopShop(String id, String title, String currency, List<ShopItem> items) {
            this.id = id;
            this.title = title;
            this.currency = currency;
            this.items = items;
        }

        @SuppressWarnings("unchecked")
        public static ShopShop fromMap(Map<String, Object> map) {
            String id = Objects.toString(map.get("id"), "default");
            String title = Objects.toString(map.get("title"), "§7Shop");
            String currency = Objects.toString(map.get("currency"), "coins");

            List<Map<String, Object>> itemMaps = (List<Map<String, Object>>) map.getOrDefault("items", new ArrayList<>());
            List<ShopItem> items = new ArrayList<>();

            for (Map<String, Object> itemMap : itemMaps) {
                items.add(ShopItem.fromMap(itemMap, currency));
            }

            return new ShopShop(id, title, currency, items);
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getCurrency() { return currency; }
        public List<ShopItem> getItems() { return items; }

        public Map<Integer, ShopItem> getItemMapBySlot() {
            Map<Integer, ShopItem> slotMap = new HashMap<>();
            for (ShopItem item : items) {
                slotMap.put(item.getSlot(), item);
            }
            return slotMap;
        }
    }
}
