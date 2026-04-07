package com.github.cybellereaper.scratchy.domain;

import java.util.HashMap;
import java.util.Map;

public record ActionStep(String actionType, Map<String, Object> args) implements ScriptStep {
    public ActionStep {
        args = args == null ? new HashMap<>() : new HashMap<>(args);
    }
}
