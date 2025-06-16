package net.kappasmp.kappaessentials.config;

import java.util.HashMap;
import java.util.Map;

public class HomeConfig {

    // Default number of homes all players get
    public int defaultLimit = 5;

    // Rank-specific home limits (can be hello/permission node names)
    public Map<String, Integer> rankLimits = new HashMap<>();

    // Teleport behavior
    public boolean cancelOnMove = true;
    public int teleportDelaySeconds = 5;
    public int cooldownSeconds = 60;
    public boolean crossDimensionAllowed = true;
    public boolean showCountdownMessages = true;

    public HomeConfig() {
        // Default rank limits (can be adjusted in config.json)
        rankLimits.put("group.og", 10);
        rankLimits.put("group.owner", 20);
    }
}
