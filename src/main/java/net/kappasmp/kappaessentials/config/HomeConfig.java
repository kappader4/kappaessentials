package net.kappasmp.kappaessentials.config;

import java.util.HashMap;
import java.util.Map;

public class HomeConfig {
    public int defaultLimit = 5;
    public Map<String, Integer> rankLimits = new HashMap<>() {{
        put("group.og", 10);
        put("group.owner", 20);
    }};

    public boolean cancelOnMove = true;
    public int teleportDelaySeconds = 5;
    public int cooldownSeconds = 60;
    public boolean crossDimensionAllowed = true;
    public boolean showCountdownMessages = true;
}
