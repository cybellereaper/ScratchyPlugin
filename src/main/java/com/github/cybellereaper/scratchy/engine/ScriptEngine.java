package com.github.cybellereaper.scratchy.engine;

import com.github.cybellereaper.scratchy.domain.*;
import com.github.cybellereaper.scratchy.registry.ActionRegistry;
import com.github.cybellereaper.scratchy.registry.ConditionRegistry;

import java.util.concurrent.CompletableFuture;

public class ScriptEngine {
    private static final int DEFAULT_WHILE_GUARD = 100;

    private final ActionRegistry actionRegistry;
    private final ConditionRegistry conditionRegistry;

    public ScriptEngine(ActionRegistry actionRegistry, ConditionRegistry conditionRegistry) {
        this.actionRegistry = actionRegistry;
        this.conditionRegistry = conditionRegistry;
    }

    public CompletableFuture<ExecutionSignal> execute(ScriptDefinition script, ScriptExecutionContext context) {
        return executeStep(script.root(), context)
                .exceptionally(ex -> {
                    context.logger().warning("Script execution failed for '" + script.name() + "': " + ex.getMessage());
                    return ExecutionSignal.STOP;
                });
    }

    private CompletableFuture<ExecutionSignal> executeStep(ScriptStep step, ScriptExecutionContext context) {
        if (step instanceof SequenceStep sequence) {
            return executeSequence(sequence, context, 0);
        }
        if (step instanceof ActionStep actionStep) {
            return actionRegistry.find(actionStep.actionType())
                    .map(handler -> handler.execute(actionStep, context))
                    .orElseGet(() -> {
                        context.logger().warning("Unknown action: " + actionStep.actionType());
                        return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
                    });
        }
        if (step instanceof IfStep ifStep) {
            boolean result = conditionRegistry.find(ifStep.condition().type())
                    .map(handler -> handler.evaluate(ifStep.condition(), context))
                    .orElse(false);
            ScriptStep branch = result ? ifStep.thenStep() : ifStep.elseStep();
            if (branch == null) {
                return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
            }
            return executeStep(branch, context);
        }
        if (step instanceof RepeatNStep repeatNStep) {
            return executeRepeatN(repeatNStep, context, 0);
        }
        if (step instanceof RepeatWhileStep repeatWhileStep) {
            return executeRepeatWhile(repeatWhileStep, context, 0);
        }
        if (step instanceof StopStep) {
            return CompletableFuture.completedFuture(ExecutionSignal.STOP);
        }
        return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
    }

    private CompletableFuture<ExecutionSignal> executeSequence(SequenceStep sequence, ScriptExecutionContext context, int index) {
        if (index >= sequence.steps().size()) {
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        }
        return executeStep(sequence.steps().get(index), context)
                .thenCompose(signal -> signal == ExecutionSignal.STOP
                        ? CompletableFuture.completedFuture(ExecutionSignal.STOP)
                        : executeSequence(sequence, context, index + 1));
    }

    private CompletableFuture<ExecutionSignal> executeRepeatN(RepeatNStep repeat, ScriptExecutionContext context, int current) {
        if (current >= repeat.times()) {
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        }
        return executeStep(repeat.body(), context)
                .thenCompose(signal -> signal == ExecutionSignal.STOP
                        ? CompletableFuture.completedFuture(ExecutionSignal.STOP)
                        : executeRepeatN(repeat, context, current + 1));
    }

    private CompletableFuture<ExecutionSignal> executeRepeatWhile(RepeatWhileStep repeat, ScriptExecutionContext context, int iterations) {
        int max = repeat.maxIterations() <= 0 ? DEFAULT_WHILE_GUARD : repeat.maxIterations();
        if (iterations >= max) {
            context.logger().warning("repeat_while reached maxIterations=" + max + ", aborting loop");
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        }
        boolean conditionResult = conditionRegistry.find(repeat.condition().type())
                .map(handler -> handler.evaluate(repeat.condition(), context))
                .orElse(false);
        if (!conditionResult) {
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        }
        return executeStep(repeat.body(), context)
                .thenCompose(signal -> signal == ExecutionSignal.STOP
                        ? CompletableFuture.completedFuture(ExecutionSignal.STOP)
                        : executeRepeatWhile(repeat, context, iterations + 1));
    }
}
