package com.github.cybellereaper.scratchy.registry;

import com.github.cybellereaper.scratchy.domain.ConditionSpec;
import com.github.cybellereaper.scratchy.engine.ScriptExecutionContext;

@FunctionalInterface
public interface ConditionHandler {
    boolean evaluate(ConditionSpec condition, ScriptExecutionContext context);
}
