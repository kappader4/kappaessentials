package net.kappasmp.kappaessentials.config;

/**
 * Main wrapper for the plugin's configuration.
 * This class is serialized/deserialized using Gson.
 */
public class ModConfigWrapper {

    /** Configuration related to home functionality */
    public HomeConfig homes = new HomeConfig();
}
