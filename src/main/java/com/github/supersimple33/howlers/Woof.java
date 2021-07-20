package com.github.supersimple33.howlers;

import java.util.List;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class implemments the main function of the howlers plugin.
*/
public class Woof extends JavaPlugin {

    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    public void onEnable() {
        getLogger().info("Starting up Woof");

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
                    if (phase == 8) {
                        List<Entity> ents = world.getEntities();
                        for (Entity ent : ents) {
                            // If the entity is a wolf, generate random and play howl sound
                            if (ent.getType() == EntityType.WOLF && Math.random() > 0.2) { // tune to adjust freq
                                // may need some tunning could also play only to nearby players, refactor for loop
                                for (Player player : server.getOnlinePlayers()) {
                                    player.playSound(ent.getLocation(), Sound.ENTITY_WOLF_HOWL, 1.0f, 1.0f);
                                }
                            }
                        }
                    }
                }

                // Set the runnable to repeat
                int nextDelay = (int) ((Math.random() * 801) + 400);
                this.runTaskLater(Woof.this, nextDelay);
            }
        };

        runnable.runTaskLater(this, 1200);
    }

    @Override
    public void onDisable() {
        getLogger().info("Nothing to save or close out, Bye");
    }
}
