package net.kappasmp.kappaessentials.teleport;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportRequestManager {

    public enum Type {
        TPA, TPAHERE
    }

    private static class Request {
        public final UUID requesterUuid;
        public final Type type;

        public Request(UUID requesterUuid, Type type) {
            this.requesterUuid = requesterUuid;
            this.type = type;
        }

        public Player getRequester() {
            return Bukkit.getPlayer(requesterUuid);
        }
    }

    // Maps target UUID to the request they received
    private static final Map<UUID, Request> requests = new HashMap<>();

    public static void sendRequest(Player from, Player to, Type type) {
        requests.put(to.getUniqueId(), new Request(from.getUniqueId(), type));
    }

    public static Player getRequester(Player receiver) {
        Request req = requests.get(receiver.getUniqueId());
        return (req != null) ? req.getRequester() : null;
    }

    public static Type getType(Player receiver) {
        Request req = requests.get(receiver.getUniqueId());
        return (req != null) ? req.type : null;
    }

    public static void clearRequest(Player receiver) {
        requests.remove(receiver.getUniqueId());
    }

    public static boolean hasRequest(Player receiver) {
        return requests.containsKey(receiver.getUniqueId());
    }
}
