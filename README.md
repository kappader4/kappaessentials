# KappaEssentials

A simple all-in-one economy + utilities mod for your **Fabric** or **Quilt** server!  
Includes: Customizable GUI Shop, Dual Currency Support, Teleports, Tokens, and more.

---

## 💡 Why Choose KappaEssentials?

KappaEssentials _(formerly **KappaEconomy**)_ is one of the only mods that brings a **DonutSMP-style** economy and commands to Fabric/Quilt-based servers.

- 💰 Coins + 🎟️ Tokens = dual currency support
- 🛒 Fully **configurable JSON shop system**
- 🎯 Custom item commands with GUI slot/format control
- 🔁 Reloadable in-game — no restart required!
- 💬 Fast Discord support + regular updates

---

## 🧪 Commands

<details>
<summary>Click to expand full command list</summary>

- `/shop` – Opens the shop GUI
- `/bal [player]` – Shows coin balance
- `/tokens` – Shows token balance
- `/pay <player> <amount>` – Transfer coins
- `/tokengive` – Grant tokens via console
- `/sell <inventory>` – (if implemented) Sell items
- `/baltop`, `/tokentop` – Leaderboards
- `/bounty`, `/bounty <player>` – Place/view bounties

### Teleportation:
- `/home`, `/sethome`, `/delhome`
- `/tpa <player>`, `/tpahere`, `/tpaccept`, `/tpdeny`

### Admin:
- `/kappaessentials reload` – Reloads all config files

</details>

---

## ⚙️ Installation Instructions

1. **Download the `.jar`** from [Modrinth](https://modrinth.com/mod/kappaessentials) or [GitHub Releases](https://github.com/kappader4/kappaessentials/releases)
2. Drop it into your `/mods` folder
3. Restart your server
4. A folder named `KappaEssentials` will appear inside `/config`
5. Customize your economy, prices, shop, and balances inside the generated config files!

---

## 📁 Configuration Files

- `shop.json`: Define all shop categories and items
- `balances.json`: Stores each player's coin balance
- `tokens.json`: Tracks player token balances
- `prices.json`: Optional item pricing
- `homes.json`: Home Data stored here
- `config.json`: Adds custumization to the mod

### Example `shop.json` format:
```json
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
                      "customCommand": "/give %player% spawner[block_entity_data={id:\"mob_spawner\",SpawnData:{entity:{id:\"minecraft:creeper\"}}}] 1"
                    },
                    {
                      "slot": 12,
                      "id": "minecraft:spawner",
                      "price": 150,
                      "amount": 1,
                      "customCommand": "/give %player% spawner[block_entity_data={id:\"mob_spawner\",SpawnData:{entity:{id:\"minecraft:zombie\"}}}] 1"
                    },
                    {
                      "slot": 13,
                      "id": "minecraft:spawner",
                      "price": 400,
                      "amount": 1,
                      "customCommand": "/give %player% spawner[block_entity_data={id:\"mob_spawner\",SpawnData:{entity:{id:\"minecraft:skeleton\"}}}] 1"
                    },
                    {
                      "slot": 14,
                      "id": "minecraft:spawner",
                      "price": 50,
                      "amount": 1,
                      "customCommand": "/give %player% spawner[block_entity_data={id:\"mob_spawner\",SpawnData:{entity:{id:\"minecraft:cow\"}}}] 1"
                    },
                    {
                      "slot": 15,
                      "id": "minecraft:spawner",
                      "price": 1250,
                      "amount": 1,
                      "customCommand": "/give %player% spawner[block_entity_data={id:\"mob_spawner\",SpawnData:{entity:{id:\"minecraft:iron_golem\"}}}] 1"
                    },
                    {
                      "slot": 4,
                      "id": "minecraft:netherite_sword",
                      "price": 500,
                      "amount": 1,
                      "customCommand": "/give %player% netherite_sword[custom_name='[{\"text\":\"ᴛʜᴇ ᴢᴇɴɪᴛʜ\",\"italic\":false,\"color\":\"dark_purple\",\"bold\":true}]',rarity=epic,enchantments={levels:{bane_of_arthropods:5,knockback:2,looting:5,mending:1,sharpness:10,unbreaking:5,silk_touch:1,smite:5,sweeping_edge:5}}]"
                    },
                    {
                      "slot": 5,
                      "id": "minecraft:netherite_axe",
                      "price": 400,
                      "amount": 1,
                      "customCommand": "/give %player% netherite_axe[custom_name='[{\"text\":\"ʟᴇɢᴇɴᴅᴀʀʏ ᴀхᴇ\",\"italic\":false,\"color\":\"gold\",\"bold\":true}]',rarity=epic,enchantments={levels:{bane_of_arthropods:5,efficiency:7,knockback:2,mending:1,sharpness:10,silk_touch:1,unbreaking:5,smite:5}}]"
                    },
                    {
                      "slot": 3,
                      "id": "minecraft:netherite_pickaxe",
                      "price": 300,
                      "amount": 1,
                      "customCommand": "/give %player% netherite_pickaxe[custom_name='[{\"text\":\"ʟᴇɢᴇɴᴅᴀʀʏ ᴘɪᴄᴋᴀхᴇ\",\"italic\":false,\"color\":\"gold\",\"bold\":true}]',rarity=epic,enchantments={levels:{efficiency:8,fortune:5,mending:1,unbreaking:6}}]"
                    }
                  ]
                }
              ]
            }
