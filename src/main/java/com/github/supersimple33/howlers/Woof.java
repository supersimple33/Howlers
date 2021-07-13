package com.github.supersimple33.howlers;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class implemments the main function of the howlers plugin.
*/
public class Woof extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Hello, SpigotMC!");
    }

    @Override
    public void onDisable() {
        getLogger().info("See you again, SpigotMC!");
    }
}
