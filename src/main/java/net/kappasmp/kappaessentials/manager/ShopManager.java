package net.kappasmp.kappaessentials.manager;

import net.kappasmp.kappaessentials.model.ShopModels.ShopCategory;
import net.kappasmp.kappaessentials.model.ShopModels.ShopShop;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ShopManager {

    private static final Yaml YAML;
    private static File shopFile;

    private static List<ShopCategory> mainMenu = new ArrayList<>();
    private static List<ShopShop> shops = new ArrayList<>();

    static {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        YAML = new Yaml(options);
    }

    public static void init(JavaPlugin plugin) {
        File configDir = plugin.getDataFolder();
        shopFile = new File(configDir, "shop.yml");

        if (!shopFile.exists()) {
            saveDefault();
        }

        load();
    }

    @SuppressWarnings("unchecked")
    public static void load() {
        try (InputStream input = new FileInputStream(shopFile)) {
            Map<String, Object> data = YAML.load(input);

            if (data != null) {
                Object menu = data.get("mainMenu");
                Object shopList = data.get("shops");

                if (menu instanceof List<?> menuList) {
                    mainMenu = menuList.stream()
                            .filter(m -> m instanceof Map)
                            .map(m -> ShopCategory.fromMap((Map<String, Object>) m))
                            .collect(Collectors.toList());
                }

                if (shopList instanceof List<?> shopDataList) {
                    shops = shopDataList.stream()
                            .filter(s -> s instanceof Map)
                            .map(s -> ShopShop.fromMap((Map<String, Object>) s))
                            .collect(Collectors.toList());
                }

                System.out.println("[KappaEssentials] Loaded shop.yml");
            } else {
                System.err.println("[KappaEssentials] shop.yml is empty or invalid.");
            }

        } catch (Exception e) {
            System.err.println("[KappaEssentials] Failed to load shop.yml: " + e.getMessage());
        }
    }

    public static void saveDefault() {
        try {
            shopFile.getParentFile().mkdirs();

            try (Writer writer = new FileWriter(shopFile)) {
                writer.write(DEFAULT_YAML);
            }

        } catch (IOException e) {
            System.err.println("[KappaEssentials] Failed to write default shop.yml: " + e.getMessage());
        }
    }

    public static List<ShopCategory> getMainMenu() {
        return mainMenu != null ? mainMenu : Collections.emptyList();
    }

    public static Optional<ShopShop> getShopById(String id) {
        return shops.stream()
                .filter(shop -> id.equalsIgnoreCase(shop.getId()))
                .findFirst();
    }

    public static List<ShopShop> getShops() {
        return shops != null ? shops : Collections.emptyList();
    }

    private static final String DEFAULT_YAML = """ 
            mainMenu:
              - name: "§dᴇɴᴅ ѕʜᴏᴘ"
                icon: "end_stone"
                slot: 11
                shopId: "endshop"

              - name: "§6ɴᴇᴛʜᴇʀ ѕʜᴏᴘ"
                icon: "netherrack"
                slot: 12
                shopId: "nethershop"

              - name: "§cᴄᴘᴠᴘ ѕʜᴏᴘ"
                icon: "end_crystal"
                slot: 13
                shopId: "cpvp"

              - name: "§aꜰᴏᴏᴅ ѕʜᴏᴘ"
                icon: "cooked_beef"
                slot: 14
                shopId: "foodshop"

              - name: "§5ᴛᴏᴋᴇɴ ѕʜᴏᴘ"
                icon: "amethyst_shard"
                slot: 15
                shopId: "tokenshop"
            
            shops:
              - id: cpvp
                title: "§7 ᴄᴘᴠᴘ ѕʜᴏᴘ"
                currency: coins
                items:
                  - slot: 9
                    id: minecraft:end_crystal
                    price: 1250
                    customName: "§cEnd Crystal"
                    customCommand: "give %player% minecraft:end_crystal 1"
                    amount: 16
                  - slot: 17
                    id: minecraft:totem_of_undying
                    price: 1250
                    customName: "§cTotem of Undying"
                    customCommand: "give %player% minecraft:totem_of_undying 1"
                    amount: 1
                  - slot: 11
                    id: minecraft:golden_apple
                    price: 1750
                    customName: "§cGolden Apple x3"
                    customCommand: "give %player% minecraft:golden_apple 3"
                    amount: 3
                  - slot: 12
                    id: minecraft:respawn_anchor
                    price: 3500
                    customName: "§cRespawn Anchor"
                    customCommand: "give %player% minecraft:respawn_anchor 1"
                    amount: 1
                  - slot: 13
                    id: minecraft:glowstone
                    price: 800
                    customName: "§cGlowstone x32"
                    customCommand: "give %player% minecraft:glowstone 32"
                    amount: 32
                  - slot: 14
                    id: minecraft:experience_bottle
                    price: 1500
                    customName: "§cXP Bottle x32"
                    customCommand: "give %player% minecraft:experience_bottle 32"
                    amount: 32
                  - slot: 15
                    id: minecraft:tipped_arrow
                    price: 2000
                    customName: "§cSlow Falling Arrows x16"
                    customCommand: "give %player% minecraft:tipped_arrow{Potion:\\"minecraft:slow_falling\\"} 16"
                    amount: 16
                  - slot: 16
                    id: minecraft:ender_pearl
                    price: 900
                    customName: "§cEnder Pearls x8"
                    customCommand: "give %player% minecraft:ender_pearl 8"
                    amount: 8
                  - slot: 10
                    id: minecraft:obsidian
                    price: 1250
                    customName: "§cObsidian x8"
                    customCommand: "give %player% minecraft:obsidian 8"
                    amount: 8
            
              - id: nethershop
                title: "§7 ɴᴇᴛʜᴇʀ ѕʜᴏᴘ"
                currency: coins
                items:
                  - slot: 11
                    id: minecraft:quartz
                    price: 500
                    customName: "§6Nether Quartz x32"
                    customCommand: "give %player% minecraft:quartz 32"
                    amount: 32
                  - slot: 12
                    id: minecraft:soul_sand
                    price: 400
                    customName: "§6Soul Sand x32"
                    customCommand: "give %player% minecraft:soul_sand 32"
                    amount: 32
                  - slot: 13
                    id: minecraft:crying_obsidian
                    price: 2500
                    customName: "§6Crying Obsidian x8"
                    customCommand: "give %player% minecraft:crying_obsidian 8"
                    amount: 8
                  - slot: 14
                    id: minecraft:ghast_tear
                    price: 3000
                    customName: "§6Ghast Tear"
                    customCommand: "give %player% minecraft:ghast_tear 1"
                    amount: 1
                  - slot: 15
                    id: minecraft:magma_cream
                    price: 1500
                    customName: "§6Magma Cream x16"
                    customCommand: "give %player% minecraft:magma_cream 16"
                    amount: 16
            
              - id: endshop
                title: "§7 ᴇɴᴅ ѕʜᴏᴘ"
                currency: coins
                items:
                  - slot: 11
                    id: minecraft:shulker_shell
                    price: 500
                    customName: "§dShulker Shell"
                    customCommand: "give %player% minecraft:shulker_shell 1"
                    amount: 1
                  - slot: 12
                    id: minecraft:shulker_box
                    price: 1250
                    customName: "§dShulker Box"
                    customCommand: "give %player% minecraft:shulker_box 1"
                    amount: 1
                  - slot: 13
                    id: minecraft:chorus_fruit
                    price: 600
                    customName: "§dChorus Fruit x16"
                    customCommand: "give %player% minecraft:chorus_fruit 16"
                    amount: 16
                  - slot: 14
                    id: minecraft:ender_pearl
                    price: 200
                    customName: "§dEnder Pearls x8"
                    customCommand: "give %player% minecraft:ender_pearl 8"
                    amount: 8
                  - slot: 15
                    id: minecraft:ender_chest
                    price: 2550
                    customName: "§dEnder Chest"
                    customCommand: "give %player% minecraft:ender_chest 1"
                    amount: 1
            
              - id: foodshop
                title: "§7 ꜰᴏᴏᴅ ѕʜᴏᴘ"
                currency: coins
                items:
                  - slot: 9
                    id: minecraft:potato
                    price: 300
                    customName: "§aPotatoes x32"
                    customCommand: "give %player% minecraft:potato 32"
                    amount: 32
                  - slot: 10
                    id: minecraft:sweet_berries
                    price: 600
                    customName: "§aSweet Berries x32"
                    customCommand: "give %player% minecraft:sweet_berries 32"
                    amount: 16
                  - slot: 11
                    id: minecraft:melon_slice
                    price: 800
                    customName: "§aMelon Slices x32"
                    customCommand: "give %player% minecraft:melon_slice 32"
                    amount: 16
                  - slot: 12
                    id: minecraft:carrot
                    price: 1000
                    customName: "§aCarrots x32"
                    customCommand: "give %player% minecraft:carrot 32"
                    amount: 32
                  - slot: 13
                    id: minecraft:apple
                    price: 1600
                    customName: "§aApples x16"
                    customCommand: "give %player% minecraft:apple 16"
                    amount: 16
                  - slot: 14
                    id: minecraft:cooked_chicken
                    price: 900
                    customName: "§aCooked Chicken x16"
                    customCommand: "give %player% minecraft:cooked_chicken 16"
                    amount: 16
                  - slot: 15
                    id: minecraft:cooked_beef
                    price: 1000
                    customName: "§aCooked Beef x16"
                    customCommand: "give %player% minecraft:cooked_beef 16"
                    amount: 8
                  - slot: 16
                    id: minecraft:golden_apple
                    price: 4000
                    customName: "§aGolden Apple x3"
                    customCommand: "give %player% minecraft:golden_apple 3"
                    amount: 3
                  - slot: 17
                    id: minecraft:golden_carrot
                    price: 2500
                    customName: "§aGolden Carrot x8"
                    customCommand: "give %player% minecraft:golden_carrot 8"
                    amount: 8
               \s
              - id: tokenshop
                title: "§7ᴛᴏᴋᴇɴ ѕʜᴏᴘ"
                currency: tokens
                items:
                  - slot: 4
                    id: minecraft:netherite_pickaxe
                    price: 3500
                    customName: "§53x3 Legendary Pickaxe"
                    customCommand: "pickaxe3x3 netherite %player%"
                    amount: 1
            
                  - slot: 9
                    id: minecraft:spawner
                    price: 250
                    customName: "§5Zombie Spawner"
                    customCommand: "smartspawner give kappader4 zombie 1"
                    amount: 1
                  - slot: 10
                    id: minecraft:spawner
                    price: 250
                    customName: "§5Cow Spawner"
                    customCommand: "smartspawner give kappader4 cow 1"
                    amount: 1
                  - slot: 11
                    id: minecraft:spawner
                    price: 250
                    customName: "§5Blaze Spawner"
                    customCommand: "smartspawner give kappader4 blaze 1"
                    amount: 1
                  - slot: 12
                    id: minecraft:spawner
                    price: 250
                    customName: "§5Pig Spawner"
                    customCommand: "smartspawner give kappader4 pig 1"
                    amount: 1
                  - slot: 13
                    id: minecraft:spawner
                    price: 250
                    customName: "§5Skeleton Spawner"
                    customCommand: "smartspawner give kappader4 skeleton 1"
                    amount: 1
                  - slot: 14
                    id: minecraft:spawner
                    price: 250
                    customName: "§5Spider Spawner"
                    customCommand: "smartspawner give kappader4 spider 1"
                    amount: 1
                  - slot: 15
                    id: minecraft:spawner
                    price: 250
                    customName: "§5Creeper Spawner"
                    customCommand: "smartspawner give kappader4 creeper 1"
                    amount: 1
                  - slot: 16
                    id: minecraft:spawner
                    price: 250
                    customName: "§5Iron Golem Spawner"
                    customCommand: "smartspawner give kappader4 iron_golem 1"
                    amount: 1
                  - slot: 17
                    id: minecraft:spawner
                    price: 250
                    customName: "§5Magma Spawner"
                    customCommand: "smartspawner give kappader4 magma 1"
                    amount: 1
    """;
}
