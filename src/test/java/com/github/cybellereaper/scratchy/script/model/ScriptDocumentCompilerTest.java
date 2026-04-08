package com.github.cybellereaper.scratchy.script.model;

import com.github.cybellereaper.scratchy.domain.*;
import com.github.cybellereaper.scratchy.script.blocks.ScriptBlock;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;
import com.github.cybellereaper.scratchy.script.events.EventParameterDeclaration;
import com.github.cybellereaper.scratchy.script.events.EventParameterRef;
import com.github.cybellereaper.scratchy.script.events.EventParameterType;
import com.github.cybellereaper.scratchy.script.variables.VariableDeclaration;
import com.github.cybellereaper.scratchy.script.variables.VariableRef;
import com.github.cybellereaper.scratchy.script.variables.VariableScope;
import com.github.cybellereaper.scratchy.script.variables.VariableType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ScriptDocumentCompilerTest {

    @Test
    void compilesNestedBranchesAndTypedRefs() {
        ScriptBlock condition = new ScriptBlock(
                UUID.randomUUID(),
                "if",
                Map.of("condition", new LiteralValueRef("random_chance")),
                Map.of("then", new ScriptSequence(List.of(new ScriptBlock(UUID.randomUUID(), "send_message",
                        Map.of("message", new VariableRef("counter"), "target", new EventParameterRef("player", EventParameterType.PLAYER)), Map.of(), Map.of())))),
                Map.of()
        );

        ScriptDocument doc = new ScriptDocument(UUID.randomUUID(), "doc", TriggerSpec.command("x"), new ScriptSequence(List.of(condition)),
                List.of(new VariableDeclaration("counter", VariableType.NUMBER, VariableScope.SCRIPT, new LiteralValueRef(0))),
                List.of(new EventParameterDeclaration("player", EventParameterType.PLAYER, true)),
                Map.of());

        ScriptDefinition compiled = new ScriptDocumentCompiler().compile(doc);

        assertInstanceOf(SequenceStep.class, compiled.root());
        IfStep ifStep = (IfStep) ((SequenceStep) compiled.root()).steps().getFirst();
        SequenceStep thenSeq = (SequenceStep) ifStep.thenStep();
        ActionStep action = (ActionStep) thenSeq.steps().getFirst();
        assertEquals("${var:counter}", action.args().get("message"));
        assertEquals("${event:player}", action.args().get("target"));
    }
}
