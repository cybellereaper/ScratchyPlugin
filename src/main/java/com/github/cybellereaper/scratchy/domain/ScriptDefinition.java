package com.github.cybellereaper.scratchy.domain;

import java.util.UUID;

public record ScriptDefinition(UUID id, String name, TriggerSpec trigger, ScriptStep root) {
}
