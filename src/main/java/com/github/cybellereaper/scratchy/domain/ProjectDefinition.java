package com.github.cybellereaper.scratchy.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ProjectDefinition(UUID id, String name, List<ScriptDefinition> scripts) {
    public ProjectDefinition {
        scripts = scripts == null ? new ArrayList<>() : new ArrayList<>(scripts);
    }
}
