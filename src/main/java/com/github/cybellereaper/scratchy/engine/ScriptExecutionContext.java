package com.github.cybellereaper.scratchy.engine;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public interface ScriptExecutionContext {
    Map<String, Object> variables();

    Optional<Player> player();

    Logger logger();

    SchedulerFacade scheduler();

    default Object variable(String key) {
        return variables().get(key);
    }
}
