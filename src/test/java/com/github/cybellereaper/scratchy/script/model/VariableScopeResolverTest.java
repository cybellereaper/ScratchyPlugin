package com.github.cybellereaper.scratchy.script.model;

import com.github.cybellereaper.scratchy.domain.TriggerSpec;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;
import com.github.cybellereaper.scratchy.script.events.EventParameterDeclaration;
import com.github.cybellereaper.scratchy.script.events.EventParameterType;
import com.github.cybellereaper.scratchy.script.variables.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VariableScopeResolverTest {

    @Test
    void allowsShadowingAcrossScopesButRejectsDuplicateWithinScope() {
        ScriptDocument valid = new ScriptDocument(java.util.UUID.randomUUID(), "x", TriggerSpec.command("a"), new ScriptSequence(List.of()),
                List.of(
                        new VariableDeclaration("value", VariableType.NUMBER, VariableScope.SCRIPT, new LiteralValueRef(1)),
                        new VariableDeclaration("value", VariableType.NUMBER, VariableScope.BRANCH, new LiteralValueRef(2))
                ),
                List.of(new EventParameterDeclaration("player", EventParameterType.PLAYER, true)),
                Map.of());

        ScriptDocument invalid = new ScriptDocument(valid.id(), valid.name(), valid.trigger(), valid.root(),
                List.of(
                        new VariableDeclaration("value", VariableType.NUMBER, VariableScope.SCRIPT, new LiteralValueRef(1)),
                        new VariableDeclaration("value", VariableType.NUMBER, VariableScope.SCRIPT, new LiteralValueRef(2))
                ),
                valid.eventParameters(), valid.metadata());

        VariableScopeResolver resolver = new VariableScopeResolver();
        assertTrue(resolver.validate(valid).isEmpty());
        assertTrue(resolver.validate(invalid).orElseThrow().contains("Duplicate variable"));
    }
}
