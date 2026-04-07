package com.github.cybellereaper.scratchy.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ActionRegistry {
    private final Map<String, ActionHandler> handlers = new HashMap<>();

    public void register(String actionType, ActionHandler handler) {
        handlers.put(actionType.toLowerCase(), handler);
    }

    public Optional<ActionHandler> find(String actionType) {
        return Optional.ofNullable(handlers.get(actionType.toLowerCase()));
    }
}
