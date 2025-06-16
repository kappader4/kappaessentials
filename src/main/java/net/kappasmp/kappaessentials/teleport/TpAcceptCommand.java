package net.kappasmp.kappaessentials.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAcceptCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

        if (requester == null || type == null || !requester.isOnline()) {
            receiver.sendMessage("§cTeleport request is no longer valid.");
            TeleportRequestManager.clearRequest(receiver);
            return true;
        }

        if (type == TeleportRequestManager.Type.TPA) {
            requester.teleport(receiver.getLocation());
            requester.sendMessage("§aTeleported to " + receiver.getName());
        } else {
            receiver.teleport(requester.getLocation());
            receiver.sendMessage("§aTeleported to " + requester.getName());
        }

        TeleportRequestManager.clearRequest(receiver);
        return true;
    }
}
