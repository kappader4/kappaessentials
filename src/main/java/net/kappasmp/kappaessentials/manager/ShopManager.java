package net.kappasmp.kappaessentials.manager;

import net.kappasmp.kappaessentials.model.ShopCategory;
import net.kappasmp.kappaessentials.model.ShopConfig;
import net.kappasmp.kappaessentials.model.ShopItem;
import net.kappasmp.kappaessentials.model.ShopShop;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class ShopManager {

    private static ShopConfig shopConfig = new ShopConfig();
    private static File shopFile;

    public static void init(File pluginFolder) {
        if (!pluginFolder.exists()) pluginFolder.mkdirs();

        shopFile = new File(pluginFolder, "shop.yml");
        if (!shopFile.exists()) {
            saveDefaultShop();
        }

        loadShop();
    }

    public static void loadShop() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(shopFile);

        shopConfig = new ShopConfig();

        // Load main menu categories
        List<Map<?, ?>> categories = config.getMapList("mainMenu");
        for (Map<?, ?> entry : categories) {
            ShopCategory category = new ShopCategory();
            category.name = (String) entry.get("name");
            category.icon = (String) entry.get("icon");
            category.slot = (int) entry.get("slot");
            category.shopId = (String) entry.get("shopId");
            shopConfig.mainMenu.add(category);
        }

        // Load shops
        List<Map<?, ?>> shops = config.getMapList("shops");
        for (Map<?, ?> entry : shops) {
            ShopShop shop = new ShopShop();
            shop.id = (String) entry.get("id");
            shop.title = (String) entry.get("title");
            shop.currency = (String) entry.get("currency");

            List<Map<?, ?>> items = (List<Map<?, ?>>) entry.get("items");
            for (Map<?, ?> itemEntry : items) {
                ShopItem item = new ShopItem();
                item.slot = (int) itemEntry.get("slot");
                item.id = (String) itemEntry.get("id");
                item.price = (int) itemEntry.get("price");
                item.customName = (String) itemEntry.get("customName");
                item.customCommand = (String) itemEntry.get("customCommand");
                item.amount = (int) itemEntry.get("amount");

                if (itemEntry.containsKey("customLore")) {
                    item.customLore = (List<String>) itemEntry.get("customLore");
                }

                shop.items.add(item);
            }

            shopConfig.shops.add(shop);
        }
    }

    public static void saveShop() {
        YamlConfiguration config = new YamlConfiguration();

        List<Map<String, Object>> mainMenuList = new ArrayList<>();
        for (ShopCategory category : shopConfig.mainMenu) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", category.name);
            map.put("icon", category.icon);
            map.put("slot", category.slot);
            map.put("shopId", category.shopId);
            mainMenuList.add(map);
        }
        config.set("mainMenu", mainMenuList);

        List<Map<String, Object>> shopsList = new ArrayList<>();
        for (ShopShop shop : shopConfig.shops) {
            Map<String, Object> shopMap = new LinkedHashMap<>();
            shopMap.put("id", shop.id);
            shopMap.put("title", shop.title);
            shopMap.put("currency", shop.currency);

            List<Map<String, Object>> itemList = new ArrayList<>();
            for (ShopItem item : shop.items) {
                Map<String, Object> itemMap = new LinkedHashMap<>();
                itemMap.put("slot", item.slot);
                itemMap.put("id", item.id);
                itemMap.put("price", item.price);
                itemMap.put("customName", item.customName);
                itemMap.put("customCommand", item.customCommand);
                itemMap.put("amount", item.amount);
                if (item.customLore != null) {
                    itemMap.put("customLore", item.customLore);
                }
                itemList.add(itemMap);
            }

            shopMap.put("items", itemList);
            shopsList.add(shopMap);
        }

        config.set("shops", shopsList);

        try {
            config.save(shopFile);
        } catch (IOException e) {
            System.err.println("[KappaEssentials] Failed to save shop.yml: " + e.getMessage());
        }
    }

    public static void saveDefaultShop() {
        try (Writer writer = new FileWriter(shopFile)) {
            writer.write(DEFAULT_YAML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ShopCategory> getMainMenu() {
        return shopConfig.mainMenu;
    }

    public static Optional<ShopShop> getShopById(String id) {
        return shopConfig.shops.stream().filter(shop -> shop.id.equalsIgnoreCase(id)).findFirst();
    }

    public static int getShopCount() {
        return shopConfig.shops.size();
    }

    private static final String DEFAULT_YAML = """
# ================================================================
# 📦 KappaEssentials Shop Configuration
# This file defines the structure of your shop GUI system.
# You can configure shop categories and individual shop inventories.
# ================================================================

# -------------------------------
# Main Menu (shop categories)
# Each entry defines a clickable category in the main GUI.
# Use "shopId" to link it to a specific shop defined below.
# -------------------------------
mainMenu:
  - name: "§cᴘᴠᴘ ѕʜᴏᴘ"
    icon: "minecraft:totem_of_undying"
    slot: 11
    shopId: "pvp"
  - name: "§6ꜰᴏᴏᴅ ѕʜᴏᴘ"
    icon: "minecraft:cooked_beef"
    slot: 12
    shopId: "food"
  - name: "§dᴇɴᴅ ѕʜᴏᴘ"
    icon: "minecraft:ender_pearl"
    slot: 13
    shopId: "end"
  - name: "§4ɴᴇᴛʜᴇʀ ѕʜᴏᴘ"
    icon: "minecraft:netherrack"
    slot: 14
    shopId: "nether"
  - name: "§5ᴛᴏᴋᴇɴ ѕʜᴏᴘ"
    icon: "minecraft:amethyst_shard"
    slot: 15
    shopId: "token"

# -------------------------------
# Shops
# Each shop defines its title, currency, and items.
# Supported currencies: "coins" or "tokens"
# -------------------------------
shops:
  - id: "pvp"
    title: "§7> ᴘᴠᴘ ѕʜᴏᴘ"
    currency: "coins"
    items:
      - slot: 10
        id: "minecraft:totem_of_undying"
        price: 1250
        customName: "§6Totem of Undying"
        customCommand: "give %player% minecraft:totem_of_undying 1"
        amount: 1
      - slot: 11
        id: "minecraft:end_crystal"
        price: 900
        customName: "§6End Crystal"
        customCommand: "give %player% minecraft:end_crystal 16"
        amount: 16
      - slot: 12
        id: "minecraft:obsidian"
        price: 550
        customName: "§6Obsidian"
        customCommand: "give %player% minecraft:obsidian 64"
        amount: 64
      - slot: 13
        id: "minecraft:respawn_anchor"
        price: 1100
        customName: "§6Respawn Anchor"
        customCommand: "give %player% minecraft:respawn_anchor 4"
        amount: 4
      - slot: 14
        id: "minecraft:glowstone"
        price: 900
        customName: "§6Glowstone"
        customCommand: "give %player% minecraft:glowstone 32"
        amount: 32
      - slot: 15
        id: "minecraft:golden_apple"
        price: 900
        customName: "§6Golden Apple"
        customCommand: "give %player% minecraft:golden_apple 8"
        amount: 8
      - slot: 16
        id: "minecraft:ender_pearl"
        price: 200
        customName: "§6Ender Pearl"
        customCommand: "give %player% minecraft:ender_pearl 16"
        amount: 16

  - id: "food"
    title: "§7> ꜰᴏᴏᴅ ѕʜᴏᴘ"
    currency: "coins"
    items:
      - slot: 11
        id: "minecraft:cooked_beef"
        price: 100
        customName: "§6Cooked Beef"
        customCommand: "give %player% minecraft:cooked_beef 32"
        amount: 32
      - slot: 12
        id: "minecraft:cooked_chicken"
        price: 900
        customName: "§6Cooked Chicken"
        customCommand: "give %player% minecraft:cooked_chicken 16"
        amount: 16
      - slot: 13
        id: "minecraft:golden_apple"
        price: 900
        customName: "§6Golden Apple"
        customCommand: "give %player% minecraft:golden_apple 8"
        amount: 8
      - slot: 14
        id: "minecraft:golden_carrot"
        price: 50
        customName: "§6Golden Carrot"
        customCommand: "give %player% minecraft:golden_carrot 4"
        amount: 4
      - slot: 15
        id: "minecraft:cooked_porkchop"
        price: 100
        customName: "§6Cooked Porkchop"
        customCommand: "give %player% minecraft:cooked_porkchop 32"
        amount: 32

  - id: "end"
    title: "§7> ᴇɴᴅ ѕʜᴏᴘ"
    currency: "coins"
    items:
      - slot: 11
        id: "minecraft:ender_pearl"
        price: 200
        customName: "§6Ender Pearl"
        customCommand: "give %player% minecraft:ender_pearl 16"
        amount: 16
      - slot: 12
        id: "minecraft:ender_chest"
        price: 900
        customName: "§6Ender Chest"
        customCommand: "give %player% minecraft:ender_chest 16"
        amount: 16
      - slot: 13
        id: "minecraft:shulker_box"
        price: 750
        customName: "§6Shulker Box"
        customCommand: "give %player% minecraft:shulker_box 1"
        amount: 1
      - slot: 14
        id: "minecraft:firework_rocket"
        price: 1000
        customName: "§6Firework Rocket"
        customCommand: "give %player% minecraft:firework_rocket 32"
        amount: 32
      - slot: 15
        id: "minecraft:elytra"
        price: 300000
        customName: "§6Elytra"
        customCommand: "give %player% minecraft:elytra 1"
        amount: 1

  - id: "nether"
    title: "§7> ɴᴇᴛʜᴇʀ ѕʜᴏᴘ"
    currency: "coins"
    items:
      - slot: 11
        id: "minecraft:netherrack"
        price: 200
        customName: "§6Netherrack"
        customCommand: "give %player% minecraft:netherrack 32"
        amount: 32
      - slot: 12
        id: "minecraft:glowstone"
        price: 900
        customName: "§6Glowstone"
        customCommand: "give %player% minecraft:glowstone 32"
        amount: 32
      - slot: 13
        id: "minecraft:quartz"
        price: 750
        customName: "§6Quartz"
        customCommand: "give %player% minecraft:quartz 32"
        amount: 32
      - slot: 14
        id: "minecraft:magma_cream"
        price: 1000
        customName: "§6Magma Cream"
        customCommand: "give %player% minecraft:magma_cream 1"
        amount: 1
      - slot: 15
        id: "minecraft:magma_block"
        price: 550
        customName: "§6Magma Block"
        customCommand: "give %player% minecraft:magma_block 32"
        amount: 32

  - id: "token"
    title: "§7> ᴛᴏᴋᴇɴ ѕʜᴏᴘ"
    currency: "tokens"
    items:
      - slot: 11
        id: "minecraft:spawner"
        price: 200
        customName: "§5Creeper Spawner"
        customCommand: "give %player% minecraft:spawner 1"
        amount: 1
      - slot: 12
        id: "minecraft:spawner"
        price: 150
        customName: "§5Zombie Spawner"
        customCommand: "give %player% minecraft:spawner 1"
        amount: 1
      - slot: 13
        id: "minecraft:spawner"
        price: 400
        customName: "§5Skeleton Spawner"
        customCommand: "give %player% minecraft:spawner 1"
        amount: 1
      - slot: 14
        id: "minecraft:spawner"
        price: 50
        customName: "§5Cow Spawner"
        customCommand: "give %player% minecraft:spawner 1"
        amount: 1
      - slot: 15
        id: "minecraft:spawner"
        price: 1250
        customName: "§5Iron Golem Spawner"
        customCommand: "give %player% minecraft:spawner 1"
        amount: 1
      - slot: 4
        id: "minecraft:netherite_sword"
        price: 2000
        customName: "§5ᴛʜᴇ ᴢᴇɴɪᴛʜ"
        customCommand: "give %player% minecraft:netherite_sword 1"
        amount: 1
        customLore:
          - "§7Click to purchase"
          - "§bEnchantments:"
          - "§7• Sharpness X"
          - "§7• Smite V"
          - "§7• Bane of Arthropods V"
          - "§7• Sweeping Edge V"
          - "§7• Knockback II"
          - "§7• Looting V"
          - "§7• Silk Touch"
          - "§7• Unbreaking V"
          - "§7• Mending"
          - "§6Token Cost: §e2000"
      - slot: 5
        id: "minecraft:netherite_axe"
        price: 1000
        customName: "§6ʟᴇɢᴇɴᴅᴀʀʏ ᴀхᴇ"
        customCommand: "give %player% minecraft:netherite_axe 1"
        amount: 1
        customLore:
          - "§7Click to purchase"
          - "§bEnchantments:"
          - "§7• Sharpness X"
          - "§7• Smite V"
          - "§7• Bane of Arthropods V"
          - "§7• Knockback II"
          - "§7• Efficiency VII"
          - "§7• Silk Touch"
          - "§7• Unbreaking V"
          - "§7• Mending"
          - "§6Token Cost: §e1000"
      - slot: 3
        id: "minecraft:netherite_pickaxe"
        price: 1000
        customName: "§6ʟᴇɢᴇɴᴅᴀʀʏ ᴘɪᴄᴋᴀхᴇ"
        customCommand: "give %player% minecraft:netherite_pickaxe 1"
        amount: 1
        customLore:
          - "§7Click to purchase"
          - "§bEnchantments:"
          - "§7• Efficiency VIII"
          - "§7• Fortune V"
          - "§7• Unbreaking VI"
          - "§7• Mending"
          - "§6Token Cost: §e1000"
    """;
}