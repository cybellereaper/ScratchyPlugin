package com.github.cybellereaper.scratchy.domain;

public record IfStep(ConditionSpec condition, ScriptStep thenStep, ScriptStep elseStep) implements ScriptStep {
}
