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
            // Welcome to the KappaEssentials shop.json file!
                                                       {
                                                                               "mainMenu": [\s
                                                                                 {
                                                                                   "name": "§cᴘᴠᴘ ѕʜᴏᴘ",
                                                                                   "icon": "minecraft:totem_of_undying",
                                                                                   "slot": 11,
                                                                                   "shopId": "pvp" // use shopId to reference what it will open when clicked on
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
                                                                                   "currency": "coins", // you can chose for either "coins" or "tokens", depends what currency you want to use
                                                                                   "items": [
                                                                                     {
                                                                                       "slot": 10, // displays what slot any item has, so where it should be
                                                                                       "id": "minecraft:totem_of_undying",
                                                                                       "price": 1250,
                                                                                       "customName": "§6Totem of Undying",
                                                                                       "customCommand": "/give %player% minecraft:totem_of_undying 1", // use any number you have specified in amount, and reference it after the Id
                                                                                       "amount": 1 //use amount for what amount it displays on screen
                                                                                     },
                                                                                     {
                                                                                       "slot": 11,
                                                                                       "id": "minecraft:end_crystal",
                                                                                       "price": 900,
                                                                                       "customName": "§6End Crystal",
                                                                                       "customCommand": "/give %player% minecraft:end_crystal 16",
                                                                                       "amount": 16
                                                                                     },
                                                                                     {
                                                                                       "slot": 12,
                                                                                       "id": "minecraft:obsidian",
                                                                                       "price": 550,
                                                                                       "customName": "§6Obsidian",
                                                                                       "customCommand": "/give %player% minecraft:obsidian 64",
                                                                                       "amount": 64
                                                                                     },
                                                                                     {
                                                                                       "slot": 13,
                                                                                       "id": "minecraft:respawn_anchor",
                                                                                       "price": 1100,
                                                                                       "customName": "§6Respawn Anchor",
                                                                                       "customCommand": "/give %player% minecraft:respawn_anchor 4",
                                                                                       "amount": 4
                                                                                     },
                                                                                     {
                                                                                       "slot": 14,
                                                                                       "id": "minecraft:glowstone",
                                                                                       "price": 900,
                                                                                       "customName": "§6Glowstone",
                                                                                       "customCommand": "/give %player% minecraft:glowstone 32",
                                                                                       "amount": 32
                                                                                     },
                                                                                     {
                                                                                       "slot": 15,
                                                                                       "id": "minecraft:golden_apple",
                                                                                       "price": 900,
                                                                                       "customName": "§6Golden Apple",
                                                                                       "customCommand": "/give %player% minecraft:golden_apple 8",
                                                                                       "amount": 8
                                                                                     },
                                                                                     {
                                                                                       "slot": 16,
                                                                                       "id": "minecraft:ender_pearl",
                                                                                       "price": 200,
                                                                                       "customName": "§6Ender Pearl",
                                                                                       "customCommand": "/give %player% minecraft:ender_pearl 16",
                                                                                       "amount": 16
                                                                                     }
                                                                                   ]
                                                                                 },
                                                                                 {
                                                                                   "id": "food",
                                                                                   "title": "§7> ꜰᴏᴏᴅ ѕʜᴏᴘ",
                                                                                   "currency": "coins",
                                                                                   "items": [
                                                                                     {
                                                                                       "slot": 11,
                                                                                       "id": "minecraft:cooked_beef",
                                                                                       "price": 100,
                                                                                       "customName": "§6Cooked Beef",
                                                                                       "customCommand": "/give %player% minecraft:cooked_beef 32",
                                                                                       "amount": 32
                                                                                     },
                                                                                     {
                                                                                       "slot": 12,
                                                                                       "id": "minecraft:cooked_chicken",
                                                                                       "price": 900,
                                                                                       "customName": "§6Cooked Chicken",
                                                                                       "customCommand": "/give %player% minecraft:cooked_chicken 16",
                                                                                       "amount": 16
                                                                                     },
                                                                                     {
                                                                                       "slot": 13,
                                                                                       "id": "minecraft:golden_apple",
                                                                                       "price": 900,
                                                                                       "customName": "§6Golden Apple",
                                                                                       "customCommand": "/give %player% minecraft:golden_apple 8",
                                                                                       "amount": 8
                                                                                     },
                                                                                     {
                                                                                       "slot": 14,
                                                                                       "id": "minecraft:golden_carrot",
                                                                                       "price": 50,
                                                                                       "customName": "§6Golden Carrot",
                                                                                       "customCommand": "/give %player% minecraft:golden_carrot 4",
                                                                                       "amount": 4
                                                                                     },
                                                                                     {
                                                                                       "slot": 15,
                                                                                       "id": "minecraft:cooked_porkchop",
                                                                                       "price": 100,
                                                                                       "customName": "§6Cooked Porkchop",
                                                                                       "customCommand": "/give %player% minecraft:cooked_porkchop 32",
                                                                                       "amount": 32
                                                                                     }
                                                                                   ]
                                                                                 },
                                                                                 {
                                                                                   "id": "end",
                                                                                   "title": "§7> ᴇɴᴅ ѕʜᴏᴘ",
                                                                                   "currency": "coins",
                                                                                   "items": [
                                                                                     {
                                                                                       "slot": 11,
                                                                                       "id": "minecraft:ender_pearl",
                                                                                       "price": 200,
                                                                                       "customName": "§6Ender Pearl",
                                                                                       "customCommand": "/give %player% minecraft:ender_pearl 16",
                                                                                       "amount": 16
                                                                                     },
                                                                                     {
                                                                                       "slot": 12,
                                                                                       "id": "minecraft:ender_chest",
                                                                                       "price": 900,
                                                                                       "customName": "§6Ender Chest",
                                                                                       "customCommand": "/give %player% minecraft:ender_chest 16",
                                                                                       "amount": 16
                                                                                     },
                                                                                     {
                                                                                       "slot": 13,
                                                                                       "id": "minecraft:shulker_box",
                                                                                       "price": 750,
                                                                                       "customName": "§6Shulker Box",
                                                                                       "customCommand": "/give %player% minecraft:shulker_box 1",
                                                                                       "amount": 1
                                                                                     },
                                                                                     {
                                                                                       "slot": 14,
                                                                                       "id": "minecraft:firework_rocket",
                                                                                       "price": 1000,
                                                                                       "customName": "§6Firework Rocket",
                                                                                       "customCommand": "/give %player% minecraft:firework_rocket 32",
                                                                                       "amount": 32
                                                                                     },
                                                                                     {
                                                                                       "slot": 15,
                                                                                       "id": "minecraft:elytra",
                                                                                       "price": 300000,
                                                                                       "customName": "§6Elytra",
                                                                                       "customCommand": "/give %player% minecraft:elytra 1",
                                                                                       "amount": 1
                                                                                     }
                                                                                   ]
                                                                                 },
                                                                                 {
                                                                                   "id": "nether",
                                                                                   "title": "§7> ɴᴇᴛʜᴇʀ ѕʜᴏᴘ",
                                                                                   "currency": "coins",
                                                                                   "items": [
                                                                                     {
                                                                                       "slot": 11,
                                                                                       "id": "minecraft:netherrack",
                                                                                       "price": 200,
                                                                                       "customName": "§6Netherrack",
                                                                                       "customCommand": "/give %player% minecraft:netherrack 32",
                                                                                       "amount": 32
                                                                                     },
                                                                                     {
                                                                                       "slot": 12,
                                                                                       "id": "minecraft:glowstone",
                                                                                       "price": 900,
                                                                                       "customName": "§6Glowstone",
                                                                                       "customCommand": "/give %player% minecraft:glowstone 32",
                                                                                       "amount": 32
                                                                                     },
                                                                                     {
                                                                                       "slot": 13,
                                                                                       "id": "minecraft:quartz",
                                                                                       "price": 750,
                                                                                       "customName": "§6Quartz",
                                                                                       "customCommand": "/give %player% minecraft:quartz 32",
                                                                                       "amount": 32
                                                                                     },
                                                                                     {
                                                                                       "slot": 14,
                                                                                       "id": "minecraft:magma_cream",
                                                                                       "price": 1000,
                                                                                       "customName": "§6Magma Cream",
                                                                                       "customCommand": "/give %player% minecraft:magma_cream 1",
                                                                                       "amount": 1
                                                                                     },
                                                                                     {
                                                                                       "slot": 15,
                                                                                       "id": "minecraft:magma_block",
                                                                                       "price": 550,
                                                                                       "customName": "§6Magma Block",
                                                                                       "customCommand": "/give %player% minecraft:magma_block 32",
                                                                                       "amount": 32
                                                                                     }
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
                                                                                       "customName": "§5Creeper Spawner",
                                                                                       "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:creeper\\"}}}] 1", // This give command is not necesary (with the \\ and whatnot), use any command that works via console
                                                                                       "amount": 1
                                                                                     },
                                                                                     {
                                                                                       "slot": 12,
                                                                                       "id": "minecraft:spawner",
                                                                                       "price": 150,
                                                                                       "customName": "§5Zombie Spawner",
                                                                                       "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:zombie\\"}}}] 1",
                                                                                       "amount": 1
                                                                                     },
                                                                                     {
                                                                                       "slot": 13,
                                                                                       "id": "minecraft:spawner",
                                                                                       "price": 400,
                                                                                       "customName": "§5Skeleton Spawner",
                                                                                       "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:skeleton\\"}}}] 1",
                                                                                       "amount": 1
                                                                                     },
                                                                                     {
                                                                                       "slot": 14,
                                                                                       "id": "minecraft:spawner",
                                                                                       "price": 50,
                                                                                       "customName": "§5Cow Spawner",
                                                                                       "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:cow\\"}}}] 1",
                                                                                       "amount": 1
                                                                                     },
                                                                                     {
                                                                                       "slot": 15,
                                                                                       "id": "minecraft:spawner",
                                                                                       "price": 1250,
                                                                                       "customName": "§5Iron Golem Spawner",
                                                                                       "customCommand": "/give %player% spawner[block_entity_data={id:\\"mob_spawner\\",SpawnData:{entity:{id:\\"minecraft:iron_golem\\"}}}] 1",
                                                                                       "amount": 1
                                                                                     },
                                                                                     {
                                                                                       "slot": 4,
                                                                                       "id": "minecraft:netherite_sword",
                                                                                       "price": 2000,
                                                                                       "customName": "§5ᴛʜᴇ ᴢᴇɴɪᴛʜ",
                                                                                       "customCommand": "/give %player% netherite_sword[custom_name='[{\\"text\\":\\"ᴛʜᴇ ᴢᴇɴɪᴛʜ\\",\\"italic\\":false,\\"color\\":\\"dark_purple\\",\\"bold\\":true}]',rarity=epic,enchantments={levels:{bane_of_arthropods:5,knockback:2,looting:5,mending:1,sharpness:10,unbreaking:5,silk_touch:1,smite:5,sweeping_edge:5}}]",
                                                                                       "customLore": [
                                                                                                             "§7Click to purchase",
                                                                                                             "§bEnchantments:",
                                                                                                             "§7• Sharpness X",
                                                                                                             "§7• Smite V",
                                                                                                             "§7• Bane of Arthropods V",
                                                                                                             "§7• Sweeping Edge V",
                                                                                                             "§7• Knockback II",
                                                                                                             "§7• Looting V",
                                                                                                             "§7• Silk Touch",
                                                                                                             "§7• Unbreaking V",
                                                                                                             "§7• Mending",
                                                                                                             "§6Token Cost: §e2000"
                                                                                                           ],
                                                                                       "amount": 1
                                                                                     },
                                                                                     {
                                                                                       "slot": 5,
                                                                                       "id": "minecraft:netherite_axe",
                                                                                       "price": 1000,
                                                                                       "customName": "§6ʟᴇɢᴇɴᴅᴀʀʏ ᴀхᴇ",
                                                                                       "customCommand": "/give %player% netherite_axe[custom_name='[{\\"text\\":\\"ʟᴇɢᴇɴᴅᴀʀʏ ᴀхᴇ\\",\\"italic\\":false,\\"color\\":\\"gold\\",\\"bold\\":true}]',rarity=epic,enchantments={levels:{bane_of_arthropods:5,efficiency:7,knockback:2,mending:1,sharpness:10,silk_touch:1,unbreaking:5,smite:5}}]",
                                                                                       "customLore": [
                                                                                                 "§7Click to purchase",
                                                                                                 "§bEnchantments:",
                                                                                                 "§7• Sharpness X",
                                                                                                 "§7• Smite V",
                                                                                                 "§7• Bane of Arthropods V",
                                                                                                 "§7• Knockback II",
                                                                                                 "§7• Efficiency VII",
                                                                                                 "§7• Silk Touch",
                                                                                                 "§7• Unbreaking V",
                                                                                                 "§7• Mending",
                                                                                                 "§6Token Cost: §e1000"
                                                                                               ],
                                                                                       "amount": 1
                                                                                     },
                                                                                     {
                                                                                       "slot": 3,
                                                                                       "id": "minecraft:netherite_pickaxe",
                                                                                       "price": 1000,
                                                                                       "customName": "§6ʟᴇɢᴇɴᴅᴀʀʏ ᴘɪᴄᴋᴀхᴇ",
                                                                                       "customCommand": "/give %player% netherite_pickaxe[custom_name='[{\\"text\\":\\"ʟᴇɢᴇɴᴅᴀʀʏ ᴘɪᴄᴋᴀхᴇ\\",\\"italic\\":false,\\"color\\":\\"gold\\",\\"bold\\":true}]',rarity=epic,enchantments={levels:{efficiency:8,fortune:5,mending:1,unbreaking:6}}]",
                                                                                       "customLore": [
                                                                                                 "§7Click to purchase",
                                                                                                 "§bEnchantments:",
                                                                                                 "§7• Efficiency VIII",
                                                                                                 "§7• Fortune V",
                                                                                                 "§7• Unbreaking VI",
                                                                                                 "§7• Mending",
                                                                                                 "§6Token Cost: §e1000"
                                                                                               ],
                                                                                       "amount": 1
                                                                                     }
                                                                                   ]
                                                                                 }
                                                                               ]
                                                                             }
    """;
}
