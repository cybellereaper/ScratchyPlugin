package com.github.cybellereaper.scratchy.context;

import com.github.cybellereaper.scratchy.engine.SchedulerFacade;
import com.github.cybellereaper.scratchy.engine.ScriptExecutionContext;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class BukkitScriptExecutionContext implements ScriptExecutionContext {
    private final Map<String, Object> variables = new HashMap<>();
    private final Player player;
    private final Logger logger;
    private final SchedulerFacade scheduler;

    public BukkitScriptExecutionContext(Player player, Logger logger, SchedulerFacade scheduler) {
        this.player = player;
        this.logger = logger;
        this.scheduler = scheduler;
    }

    @Override
    public Map<String, Object> variables() {
        return variables;
    }

    @Override
    public Optional<Player> player() {
        return Optional.ofNullable(player);
    }

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public SchedulerFacade scheduler() {
        return scheduler;
    }
}
