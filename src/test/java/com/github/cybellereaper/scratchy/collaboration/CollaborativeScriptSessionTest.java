package com.github.cybellereaper.scratchy.collaboration;

import com.github.cybellereaper.scratchy.domain.TriggerSpec;
import com.github.cybellereaper.scratchy.script.blocks.ScriptBlock;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;
import com.github.cybellereaper.scratchy.script.model.ScriptDocument;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CollaborativeScriptSessionTest {

    @Test
    void rejectsStaleRevisionsAndKeepsDeterministicState() {
        ScriptBlock a = new ScriptBlock(UUID.randomUUID(), "a", Map.of(), Map.of(), Map.of());
        ScriptBlock b = new ScriptBlock(UUID.randomUUID(), "b", Map.of(), Map.of(), Map.of());
        ScriptDocument doc = new ScriptDocument(UUID.randomUUID(), "s", TriggerSpec.command("x"), new ScriptSequence(List.of(a, b)), List.of(), List.of(), Map.of());

        CollaborativeScriptSession session = new CollaborativeScriptSession(doc);
        CollaborationResult applied = session.apply(new MoveBlockOperation(0, b.id(), 0));
        CollaborationResult conflict = session.apply(new MoveBlockOperation(0, a.id(), 1));

        assertTrue(applied.applied());
        assertFalse(conflict.applied());
        assertEquals("Stale revision", conflict.conflictReason());
        assertEquals(List.of(b.id(), a.id()), session.document().root().blocks().stream().map(ScriptBlock::id).toList());
    }
}
