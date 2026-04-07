package com.github.cybellereaper.scratchy.registry;

import com.github.cybellereaper.scratchy.domain.ActionStep;
import com.github.cybellereaper.scratchy.engine.ExecutionSignal;
import com.github.cybellereaper.scratchy.engine.ScriptExecutionContext;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface ActionHandler {
    CompletableFuture<ExecutionSignal> execute(ActionStep step, ScriptExecutionContext context);
}
