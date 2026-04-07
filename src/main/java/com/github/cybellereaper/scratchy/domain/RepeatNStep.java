package com.github.cybellereaper.scratchy.domain;

public record RepeatNStep(int times, ScriptStep body) implements ScriptStep {
}
