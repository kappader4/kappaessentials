package net.kappasmp.kappaessentials.bounty;

import net.kappasmp.kappaessentials.gui.BountyGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BountyCommand implements CommandExecutor {

    private final Plugin plugin;

    public BountyCommand(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        player.getScheduler().execute(plugin, () -> {
            BountyGui.open(player);
        }, null, 0L);

        return true;
    }
}
