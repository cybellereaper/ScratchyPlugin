package com.github.cybellereaper.scratchy.script.events;

import com.github.cybellereaper.scratchy.script.model.ValueRef;

public record EventParameterRef(String key, EventParameterType type) implements ValueRef {
}
