package com.github.cybellereaper.scratchy.domain;

public record RepeatWhileStep(ConditionSpec condition, ScriptStep body, int maxIterations) implements ScriptStep {
}
