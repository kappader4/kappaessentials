package net.kappasmp.kappaessentials.config;

import java.util.HashMap;
import java.util.Map;

public class HomeConfig {
    public int homeLimit = 3;
    public int cooldownSeconds = 10;
    public boolean crossDimension = false;
    public int defaultLimit = 3;
    public int teleportDelaySeconds = 5;
    public boolean crossDimensionAllowed = false;
    public Map<String, Integer> rankLimits = new HashMap<>();
}
