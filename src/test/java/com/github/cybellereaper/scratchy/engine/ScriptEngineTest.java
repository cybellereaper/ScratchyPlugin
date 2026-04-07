package com.github.cybellereaper.scratchy.engine;

import com.github.cybellereaper.scratchy.domain.*;
import com.github.cybellereaper.scratchy.registry.ActionRegistry;
import com.github.cybellereaper.scratchy.registry.ConditionRegistry;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ScriptEngineTest {

    @Test
    void executesSequenceInOrder() {
        ActionRegistry actions = new ActionRegistry();
        ConditionRegistry conditions = new ConditionRegistry();
        List<String> calls = new ArrayList<>();

        actions.register("a", (step, context) -> {
            calls.add("a");
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });
        actions.register("b", (step, context) -> {
            calls.add("b");
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });

        ScriptEngine engine = new ScriptEngine(actions, conditions);
        ScriptDefinition script = new ScriptDefinition(UUID.randomUUID(), "test",
                TriggerSpec.command("x"),
                new SequenceStep(List.of(new ActionStep("a", Map.of()), new ActionStep("b", Map.of()))));

        ExecutionSignal signal = engine.execute(script, new TestContext()).join();

        assertEquals(ExecutionSignal.CONTINUE, signal);
        assertEquals(List.of("a", "b"), calls);
    }

    @Test
    void stopsWhenStopSignalReturned() {
        ActionRegistry actions = new ActionRegistry();
        ConditionRegistry conditions = new ConditionRegistry();
        AtomicInteger counter = new AtomicInteger();

        actions.register("first", (step, context) -> {
            counter.incrementAndGet();
            return CompletableFuture.completedFuture(ExecutionSignal.STOP);
        });
        actions.register("second", (step, context) -> {
            counter.incrementAndGet();
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });

        ScriptEngine engine = new ScriptEngine(actions, conditions);
        ScriptDefinition script = new ScriptDefinition(UUID.randomUUID(), "stop",
                TriggerSpec.command("x"),
                new SequenceStep(List.of(new ActionStep("first", Map.of()), new ActionStep("second", Map.of()))));

        ExecutionSignal signal = engine.execute(script, new TestContext()).join();

        assertEquals(ExecutionSignal.STOP, signal);
        assertEquals(1, counter.get());
    }

    @Test
    void supportsRepeatAndIfElse() {
        ActionRegistry actions = new ActionRegistry();
        ConditionRegistry conditions = new ConditionRegistry();
        AtomicInteger count = new AtomicInteger();

        actions.register("inc", (step, context) -> {
            count.incrementAndGet();
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });
        conditions.register("always", (condition, context) -> true);

        ScriptEngine engine = new ScriptEngine(actions, conditions);
        ScriptStep root = new SequenceStep(List.of(
                new RepeatNStep(2, new ActionStep("inc", Map.of())),
                new IfStep(new ConditionSpec("always", Map.of()), new ActionStep("inc", Map.of()), null)
        ));
        ScriptDefinition script = new ScriptDefinition(UUID.randomUUID(), "logic", TriggerSpec.command("x"), root);

        ExecutionSignal signal = engine.execute(script, new TestContext()).join();

        assertEquals(ExecutionSignal.CONTINUE, signal);
        assertEquals(3, count.get());
    }

    private static class TestContext implements ScriptExecutionContext {
        private final Map<String, Object> vars = new HashMap<>();

        @Override
        public Map<String, Object> variables() {
            return vars;
        }

        @Override
        public Optional<Player> player() {
            return Optional.empty();
        }

        @Override
        public Logger logger() {
            return Logger.getLogger("test");
        }

        @Override
        public SchedulerFacade scheduler() {
            return (ticks, runnable) -> runnable.run();
        }
    }
}
