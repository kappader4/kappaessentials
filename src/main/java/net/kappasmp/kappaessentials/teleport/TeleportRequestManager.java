package net.kappasmp.kappaessentials.teleport;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class TeleportRequestManager {

    public enum Type { TPA, TPAHERE }

    private static class Request {
        public final ServerPlayerEntity requester;
        public final Type type;

        public Request(ServerPlayerEntity requester, Type type) {
            this.requester = requester;
            this.type = type;
        }
    }

    private static final Map<UUID, Request> requests = new HashMap<>();

    public static void sendRequest(ServerPlayerEntity from, ServerPlayerEntity to, Type type) {
        requests.put(to.getUuid(), new Request(from, type));
    }

    public static ServerPlayerEntity getRequester(ServerPlayerEntity receiver) {
        Request req = requests.get(receiver.getUuid());
        return req != null ? req.requester : null;
    }

    public static Type getType(ServerPlayerEntity receiver) {
        Request req = requests.get(receiver.getUuid());
        return req != null ? req.type : null;
    }

    public static void clearRequest(ServerPlayerEntity receiver) {
        requests.remove(receiver.getUuid());
    }

    public static boolean hasRequest(ServerPlayerEntity receiver) {
        return requests.containsKey(receiver.getUuid());
    }
}
