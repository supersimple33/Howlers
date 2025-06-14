package com.github.supersimple33.howlers;

import java.util.List;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * This class implements the main functions of the howlers plugin.
 */
public class Woof extends JavaPlugin {

    // Vars
    // Should have getter methods
    private double barkChance;
    private int delay;
    private int variation;
    private float volume;
    private float pitchOffset;
    private List<Integer> phases;
    private List<String> worldBlacklist;

    // Set up the BukkitRunnable that will be used to play the sound.
    @SuppressWarnings("checkstyle:magicnumber")
    private void setTask() {
        // Create runnable
        BukkitRunnable runnable = new BukkitRunnable() {

            @Override
            @SuppressWarnings("checkstyle:nestedifdepth")
            public void run() {
                Server server = getServer();
                for (World world : server.getWorlds()) {
                    // Check if the world is blacklisted
                    if (worldBlacklist.contains(world.getName())) {
                        continue; // Skip to the next world if blacklisted
                    }

                    // Check that it is nighttime
                    long time = world.getTime();
                    if (time < 12300 || time > 23850) {
                        continue; // Skip to the next world if not nighttime
                    }

                    // Check if the moon is a full moon
                    int days = (int) world.getFullTime() / 24000;
                    int phase = days % 8;

                    // Check for the right moon phase and iterate entities
                    if (!phases.contains(phase)) {
                        continue;
                    }
                    // Get a list of entities and loop through them
                    List<Entity> ents = world.getEntities();
                    for (Entity ent : ents) {
                        // If the entity is a wolf, generate random and play howl sound
                        if (ent.getType() == EntityType.WOLF && Math.random() < barkChance) {
                            // may need some tuning could also play only to nearby players, refactor for loop
                            for (Player player : server.getOnlinePlayers()) {
                                float pitch = 1.0f + pitchOffset - (2 * pitchOffset * (float) Math.random());
                                player.playSound(ent.getLocation(), Sound.ENTITY_WOLF_HOWL, volume, pitch);
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check for permissions and that the command is correct
        if (command.getName().equalsIgnoreCase("hwreload")) {

            if (sender.hasPermission("howlers.reload")) {
                sender.sendMessage("Starting Reload of Howlers Config");

                // Reload and grab the config
                reloadConfig();
                FileConfiguration config = getConfig();

                // Threadsafe?
                // Set vars to their new values
                loadConfigVariables(config);

                getLogger().info("Howlers Config Reloaded");
                return true;
            }
        }
        return false;
    }

    private void loadConfigVariables(FileConfiguration config) {
        this.barkChance = config.getDouble("ChanceOfBark");
        this.delay = config.getInt("Delay");
        this.variation = config.getInt("TimeVariation");
        this.volume = (float) config.getDouble("Volume");
        this.pitchOffset = (float) config.getDouble("PitchOffset");
        this.phases = config.getIntegerList("Phases");
        this.worldBlacklist = config.getStringList("WorldBlacklist");
    }

    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    public void onEnable() {
        // Set up config defaults
        FileConfiguration config = getConfig();
        config.addDefault("ChanceOfBark", 0.6); // chance of each wolf howling on event
        config.addDefault("Delay", 300); // delay between each event
        config.addDefault("TimeVariation", 401); // max amount of random time to add between events
        config.addDefault("Volume", 2.0); // volume of each howl
        config.addDefault("PitchOffset", 0.2);
        config.addDefault("Phases", List.of(0)); // which moon phases to play on
        config.addDefault("WorldBlacklist", List.of("world_nether", "world_the_end")); // worlds to not play in

        // Save / Set up the config
        config.options().copyDefaults(true);
        saveConfig();

        // Loading
        loadConfigVariables(config);

        setTask();
        getLogger().info("Howlers Is Ready");
    }

    @Override
    public void onDisable() {
        getLogger().info("Nothing to save or close out, Bye");
    }
}
