package net.kappasmp.kappaessentials.teleport;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportRequestManager {

    public enum Type { TPA, TPAHERE }

    private static class Request {
        public final Player requester;
        public final Type type;

        public Request(Player requester, Type type) {
            this.requester = requester;
            this.type = type;
        }
    }

    private static final Map<UUID, Request> requests = new HashMap<>();

    public static void sendRequest(Player from, Player to, Type type) {
        requests.put(to.getUniqueId(), new Request(from, type));
    }

    public static Player getRequester(Player receiver) {
        Request req = requests.get(receiver.getUniqueId());
        return req != null ? req.requester : null;
    }

    public static Type getType(Player receiver) {
        Request req = requests.get(receiver.getUniqueId());
        return req != null ? req.type : null;
    }

    public static void clearRequest(Player receiver) {
        requests.remove(receiver.getUniqueId());
    }

    public static boolean hasRequest(Player receiver) {
        return requests.containsKey(receiver.getUniqueId());
    }
}
