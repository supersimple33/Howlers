package com.github.supersimple33.howlers;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class implemments the main functions of the howlers plugin.
*/
public class Woof extends JavaPlugin {

    // Vars
    double barkChance;
    int delay;
    int variation;
    float volume;
    float pitchOffset;
    List<Integer> phases;

    // Setup the BukkitRunnable that will be used to play the sound.
    @SuppressWarnings("checkstyle:magicnumber")
    private void setTask() {
        BukkitRunnable runnable = new BukkitRunnable() {

            @Override
            @SuppressWarnings("checkstyle:nestedifdepth")
            public void run() {
                Server server = getServer();
                World world = server.getWorlds().get(0);
                long time = world.getTime(); // ensure this is the correct world

                // Check that it is nightime
                if (time > 12300 && time < 23850) {
                    int days = (int) world.getFullTime() / 24000;
                    int phase = days % 8;
                    // Check for right moon phase and iterate entities
                    if (phases.contains(phase)) {
                        List<Entity> ents = world.getEntities();
                        for (Entity ent : ents) {
                            // If the entity is a wolf, generate random and play howl sound
                            if (ent.getType() == EntityType.WOLF && Math.random() < barkChance) {
                                // may need some tunning could also play only to nearby players, refactor for loop
                                for (Player player : server.getOnlinePlayers()) {
                                    float pitch = 1.0f + pitchOffset - (2 * pitchOffset * (float) Math.random());
                                    player.playSound(ent.getLocation(), Sound.ENTITY_WOLF_HOWL, volume, pitch);
                                }
                            }
                        }
                    }
                }
                setTask();
            }
        };

        // Set the runnable to repeat
        int nextDelay = (int) ((Math.random() * variation) + delay);
        runnable.runTaskLater(this, nextDelay);
    }

    @Override
    public void onEnable() {
        // Set up config defaults
        FileConfiguration config = getConfig();
        config.addDefault("ChanceOfBark", 0.6); // chance of each wolf howling on event
        config.addDefault("Delay", 300); // delay between each event
        config.addDefault("TimeVariation", 401); // max amount of random time to add between events
        config.addDefault("Volume", 2.0); // volume of each howl
        config.addDefault("PitchOffset", 0.2);
        config.addDefault("Phases", Arrays.asList(new int[]{0})); // which moon phases to play on

        // Save/Setup the config
        config.options().copyDefaults(true);
        saveConfig();

        // Loading
        barkChance = config.getDouble("ChanceOfBark");
        delay = config.getInt("Delay");
        variation = config.getInt("TimeVariation");
        volume = (float) config.getDouble("Volume");
        pitchOffset = (float) config.getDouble("PitchOffset");
        phases = config.getIntegerList("Phases");        

        setTask();
        getLogger().info("Howlers Is Ready");
    }

    @Override
    public void onDisable() {
        getLogger().info("Nothing to save or close out, Bye");
    }
}
