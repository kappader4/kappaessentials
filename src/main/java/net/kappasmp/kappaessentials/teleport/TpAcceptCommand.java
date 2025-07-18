package net.kappasmp.kappaessentials.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class TpAcceptCommand implements CommandExecutor {

    private final Plugin plugin;

    public TpAcceptCommand(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player receiver)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!TeleportRequestManager.hasRequest(receiver)) {
            receiver.sendMessage("§cNo pending teleport requests.");
            return true;
        }

        Player requester = TeleportRequestManager.getRequester(receiver);
        TeleportRequestManager.Type type = TeleportRequestManager.getType(receiver);

        if (requester == null || !requester.isOnline() || type == null) {
            receiver.sendMessage("§cTeleport request is no longer valid.");
            TeleportRequestManager.clearRequest(receiver);
            return true;
        }

        switch (type) {
            case TPA -> {
                requester.getScheduler().run(plugin, task -> {
                    requester.teleportAsync(receiver.getLocation()).thenAccept(success -> {
                        if (success) {
                            requester.sendMessage("§aTeleported to " + receiver.getName());
                        } else {
                            requester.sendMessage("§cTeleport failed.");
                        }
                    });
                }, () -> {});
            }
            case TPAHERE -> {
                receiver.getScheduler().run(plugin, task -> {
                    receiver.teleportAsync(requester.getLocation()).thenAccept(success -> {
                        if (success) {
                            receiver.sendMessage("§aTeleported to " + requester.getName());
                        } else {
                            receiver.sendMessage("§cTeleport failed.");
                        }
                    });
                }, () -> {});
            }
        }

        TeleportRequestManager.clearRequest(receiver);
        return true;
    }
}
