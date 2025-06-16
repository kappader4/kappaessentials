package net.kappasmp.kappaessentials.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RtpGui {

    private static final Map<Integer, String> dimensionCommands = new HashMap<>();
    private static final String GUI_TITLE = ChatColor.DARK_AQUA + "§8> RTP Dimensions";

    static {
        dimensionCommands.put(11, "world");
        dimensionCommands.put(13, "world_nether");
        dimensionCommands.put(15, "world_the_end");
    }

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        gui.setItem(11, createItem(Material.GRASS_BLOCK, "§aOverworld"));
        gui.setItem(13, createItem(Material.NETHERRACK, "§cNether"));
        gui.setItem(15, createItem(Material.END_STONE, "§eThe End"));

        player.openInventory(gui);
    }

    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        event.setCancelled(true);

        int slot = event.getSlot();
        if (!dimensionCommands.containsKey(slot)) return;

        String dimension = dimensionCommands.get(slot);
        player.closeInventory();
        startCountdown(player, dimension);
    }

    private static void startCountdown(Player player, String dimension) {
        Location startLocation = player.getLocation().clone();
        UUID uuid = player.getUniqueId();
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(RtpGui.class);

        new BukkitRunnable() {
            int secondsLeft = 5;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                Location current = player.getLocation();
                if (moved(startLocation, current)) {
                    player.sendMessage(ChatColor.RED + "Teleport cancelled because you moved.");
                    cancel();
                    return;
                }

                if (secondsLeft > 0) {
                    player.sendMessage(ChatColor.GRAY + "Teleporting in " + ChatColor.YELLOW + secondsLeft + ChatColor.GRAY + " seconds...");
                    secondsLeft--;
                } else {
                    String command = "rtp player " + player.getName() + " " + dimension;
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    player.sendMessage(ChatColor.GREEN + "Teleported to " + dimension + "!");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 second
    }

    private static boolean moved(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() ||
                from.getBlockY() != to.getBlockY() ||
                from.getBlockZ() != to.getBlockZ();
    }
}
