package com.github.cybellereaper.scratchy.domain;

import java.util.HashMap;
import java.util.Map;

public record ConditionSpec(String type, Map<String, Object> args) {
    public ConditionSpec {
        args = args == null ? new HashMap<>() : new HashMap<>(args);
    }
}
