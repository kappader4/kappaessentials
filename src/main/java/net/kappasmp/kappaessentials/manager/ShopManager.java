package net.kappasmp.kappaessentials.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.kappasmp.kappaessentials.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class ShopManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ShopConfig shopConfig = new ShopConfig();
    private static File shopFile;

    public static void init(Path configDir) {
        File configFolder = configDir.resolve("KappaEssentials").toFile();
        configFolder.mkdirs();

        shopFile = new File(configFolder, "shop.json");

        if (!shopFile.exists()) {
            saveDefaultShop(); // 🔁 This will write your default JSON
        }

        loadShop();
    }

    public static void loadShop() {
        try (Reader reader = new FileReader(shopFile)) {
            shopConfig = GSON.fromJson(reader, ShopConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDefaultShop() {
        try (Writer writer = new FileWriter(shopFile)) {
            writer.write(DEFAULT_JSON);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveShop() {
        try (Writer writer = new FileWriter(shopFile)) {
            GSON.toJson(shopConfig, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ShopCategory> getMainMenu() {
        return shopConfig.mainMenu;
    }

    public static Optional<ShopShop> getShopById(String id) {
        if (shopConfig.shops == null) return Optional.empty();
        return shopConfig.shops.stream()
                .filter(shop -> shop.id.equalsIgnoreCase(id))
                .findFirst();
    }

    public static int getShopCount() {
        return shopConfig.shops != null ? shopConfig.shops.size() : 0;
    }

    // ✨ Embed your full JSON here as the default
    private static final String DEFAULT_JSON = """
            {
              "mainMenu": [
                {
                  "name": "§cᴘᴠᴘ ѕʜᴏᴘ",
                  "icon": "minecraft:totem_of_undying",
                  "slot": 11,
                  "shopId": "pvp"
                },
                {
                  "name": "§6ꜰᴏᴏᴅ ѕʜᴏᴘ",
                  "icon": "minecraft:cooked_beef",
                  "slot": 12,
                  "shopId": "food"
                },
                {
                  "name": "§dᴇɴᴅ ѕʜᴏᴘ",
                  "icon": "minecraft:ender_pearl",
                  "slot": 13,
                  "shopId": "end"
                },
                {
                  "name": "§4ɴᴇᴛʜᴇʀ ѕʜᴏᴘ",
                  "icon": "minecraft:netherrack",
                  "slot": 14,
                  "shopId": "nether"
                },
                {
                  "name": "§5ᴛᴏᴋᴇɴ ѕʜᴏᴘ",
                  "icon": "minecraft:amethyst_shard",
                  "slot": 15,
                  "shopId": "token"
                }
              ],
              "shops": [
                {
                  "id": "pvp",
                  "title": "§7> ᴘᴠᴘ ѕʜᴏᴘ",
                  "currency": "coins",
                  "items": [
                    { "slot": 10, "id": "minecraft:totem_of_undying", "price": 1250, "amount": 1 },
                    { "slot": 11, "id": "minecraft:end_crystal", "price": 900, "amount": 16 },
                    { "slot": 12, "id": "minecraft:obsidian", "price": 550, "amount": 64 },
                    { "slot": 13, "id": "minecraft:respawn_anchor", "price": 1100, "amount": 4 },
                    { "slot": 14, "id": "minecraft:glowstone", "price": 900, "amount": 32 },
                    { "slot": 15, "id": "minecraft:golden_apple", "price": 900, "amount": 8 },
                    { "slot": 16, "id": "minecraft:ender_pearl", "price": 200, "amount": 16 }
                  ]
                },
                {
                  "id": "food",
                  "title": "§7> ꜰᴏᴏᴅ ѕʜᴏᴘ",
                  "currency": "coins",
                  "items": [
                    { "slot": 11, "id": "minecraft:cooked_beef", "price": 100, "amount": 32 },
                    { "slot": 12, "id": "minecraft:cooked_chicken", "price": 900, "amount": 16 },
                    { "slot": 13, "id": "minecraft:golden_apple", "price": 900, "amount": 8 },
                    { "slot": 14, "id": "minecraft:golden_carrot", "price": 50, "amount": 4 },
                    { "slot": 15, "id": "minecraft:cooked_porkchop", "price": 100, "amount": 32 }
                  ]
                },
                {
                  "id": "end",
                  "title": "§7> ᴇɴᴅ ѕʜᴏᴘ",
                  "currency": "coins",
                  "items": [
                    { "slot": 11, "id": "minecraft:ender_pearl", "price": 200, "amount": 16 },
                    { "slot": 12, "id": "minecraft:ender_chest", "price": 900, "amount": 16 },
                    { "slot": 13, "id": "minecraft:shulker_box", "price": 750, "amount": 1 },
                    { "slot": 14, "id": "minecraft:firework_rocket", "price": 1000, "amount": 32 },
                    { "slot": 15, "id": "minecraft:elytra", "price": 300000, "amount": 1 }
                  ]
                },
                {
                  "id": "nether",
                  "title": "§7> ɴᴇᴛʜᴇʀ ѕʜᴏᴘ",
                  "currency": "coins",
                  "items": [
                    { "slot": 11, "id": "minecraft:netherrack", "price": 200, "amount": 32 },
                    { "slot": 12, "id": "minecraft:glowstone", "price": 900, "amount": 32 },
                    { "slot": 13, "id": "minecraft:quartz", "price": 750, "amount": 32 },
                    { "slot": 14, "id": "minecraft:magma_cream", "price": 1000, "amount": 1 },
                    { "slot": 15, "id": "minecraft:magma_block", "price": 550, "amount": 32 }
                  ]
                },
                {
                  "id": "token",
                  "title": "§7> ᴛᴏᴋᴇɴ ѕʜᴏᴘ",
                  "currency": "tokens",
                  "items": [
                    {
                      "slot": 11,
                      "id": "minecraft:spawner",
                      "price": 200,
                      "amount": 1,
                      "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:creeper\\"}}}] 1"
                    },
                    {
                      "slot": 12,
                      "id": "minecraft:spawner",
                      "price": 150,
                      "amount": 1,
                      "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:zombie\\"}}}] 1"
                    },
                    {
                      "slot": 13,
                      "id": "minecraft:spawner",
                      "price": 400,
                      "amount": 1,
                      "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:skeleton\\"}}}] 1"
                    },
                    {
                      "slot": 14,
                      "id": "minecraft:spawner",
                      "price": 50,
                      "amount": 1,
                      "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:cow\\"}}}] 1"
                    },
                    {
                      "slot": 15,
                      "id": "minecraft:spawner",
                      "price": 1250,
                      "amount": 1,
                      "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:iron_golem\\"}}}] 1"
                    },
                    {
                      "slot": 4,
                      "id": "minecraft:netherite_sword",
                      "price": 500,
                      "amount": 1,
                      "customCommand": "/give %player% netherite_sword[custom_name='[{\\"text\\":\\"ᴛʜᴇ ᴢᴇɴɪᴛʜ\\",\\"italic\\":false,\\"color\\":\\"dark_purple\\",\\"bold\\":true}]',rarity=epic,enchantments={levels:{bane_of_arthropods:5,knockback:2,looting:5,mending:1,sharpness:10,unbreaking:5,silk_touch:1,smite:5,sweeping_edge:5}}]"
                    },
                    {
                      "slot": 5,
                      "id": "minecraft:netherite_axe",
                      "price": 400,
                      "amount": 1,
                      "customCommand": "/give %player% netherite_axe[custom_name='[{\\"text\\":\\"ʟᴇɢᴇɴᴅᴀʀʏ ᴀхᴇ\\",\\"italic\\":false,\\"color\\":\\"gold\\",\\"bold\\":true}]',rarity=epic,enchantments={levels:{bane_of_arthropods:5,efficiency:7,knockback:2,mending:1,sharpness:10,silk_touch:1,unbreaking:5,smite:5}}]"
                    },
                    {
                      "slot": 3,
                      "id": "minecraft:netherite_pickaxe",
                      "price": 300,
                      "amount": 1,
                      "customCommand": "/give %player% netherite_pickaxe[custom_name='[{\\"text\\":\\"ʟᴇɢᴇɴᴅᴀʀʏ ᴘɪᴄᴋᴀхᴇ\\",\\"italic\\":false,\\"color\\":\\"gold\\",\\"bold\\":true}]',rarity=epic,enchantments={levels:{efficiency:8,fortune:5,mending:1,unbreaking:6}}]"
                    }
                  ]
                }
              ]
            }
    """;
}
