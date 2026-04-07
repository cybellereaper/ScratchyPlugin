package com.github.cybellereaper.scratchy.context;

import com.github.cybellereaper.scratchy.engine.SchedulerFacade;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitSchedulerFacade implements SchedulerFacade {
    private final JavaPlugin plugin;

    public BukkitSchedulerFacade(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runLater(long ticks, Runnable runnable) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, ticks);
    }
}
