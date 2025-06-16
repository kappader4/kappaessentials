package net.kappasmp.kappaessentials.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cUsage: /tpa <player>");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null || target.equals(player)) {
            player.sendMessage("§cInvalid target player.");
            return true;
        }

        TeleportRequestManager.sendRequest(player, target, TeleportRequestManager.Type.TPA);

        player.sendMessage("§aTPA request sent to " + target.getName());
        target.sendMessage("§e" + player.getName() + " wants to teleport to you. Type §a/tpaccept §eor §c/tpdeny.");

        return true;
    }
}
