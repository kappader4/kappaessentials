package net.kappasmp.kappaessentials.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDenyCommand implements CommandExecutor {

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
        if (requester != null) {
            requester.sendMessage("§cYour teleport request was denied.");
        }

        receiver.sendMessage("§7Teleport request denied.");
        TeleportRequestManager.clearRequest(receiver);

        return true;
    }
}
