package com.github.cybellereaper.scratchy.validation;

import com.github.cybellereaper.scratchy.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ScriptValidatorTest {

    private final ScriptValidator validator = new ScriptValidator();

    @Test
    void rejectsBlankCommandTriggerAndInvalidLoopSettings() {
        ScriptDefinition script = new ScriptDefinition(
                UUID.randomUUID(),
                "",
                TriggerSpec.command(""),
                new RepeatWhileStep(new ConditionSpec("", Map.of()), null, 0)
        );

        ValidationResult result = validator.validate(script);

        assertFalse(result.valid());
        assertTrue(result.errors().stream().anyMatch(message -> message.contains("name cannot be blank")));
        assertTrue(result.errors().stream().anyMatch(message -> message.contains("command key")));
        assertTrue(result.errors().stream().anyMatch(message -> message.contains("requires a condition")));
        assertTrue(result.errors().stream().anyMatch(message -> message.contains("requires a body")));
        assertTrue(result.errors().stream().anyMatch(message -> message.contains("maxIterations")));
    }

    @Test
    void acceptsSimpleValidScript() {
        ScriptDefinition script = new ScriptDefinition(
                UUID.randomUUID(),
                "ok",
                TriggerSpec.scheduled(40),
                new SequenceStep(List.of(new ActionStep("send_message", Map.of("message", "hello"))))
        );

        ValidationResult result = validator.validate(script);
        assertTrue(result.valid());
    }
}
