package com.github.cybellereaper.scratchy.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConditionRegistry {
    private final Map<String, ConditionHandler> handlers = new HashMap<>();

    public void register(String type, ConditionHandler handler) {
        handlers.put(type.toLowerCase(), handler);
    }

    public Optional<ConditionHandler> find(String type) {
        return Optional.ofNullable(handlers.get(type.toLowerCase()));
    }
}
