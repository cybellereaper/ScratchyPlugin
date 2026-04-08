package com.github.cybellereaper.scratchy.editor.interaction;

import com.github.cybellereaper.scratchy.script.blocks.ScriptBlock;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockArrangementServiceTest {

    private final BlockArrangementService service = new BlockArrangementService();

    @Test
    void movesBlockToRequestedIndex() {
        ScriptBlock a = new ScriptBlock(UUID.randomUUID(), "a", Map.of(), Map.of(), Map.of());
        ScriptBlock b = new ScriptBlock(UUID.randomUUID(), "b", Map.of(), Map.of(), Map.of());
        ScriptBlock c = new ScriptBlock(UUID.randomUUID(), "c", Map.of(), Map.of(), Map.of());

        ScriptSequence moved = service.move(new ScriptSequence(List.of(a, b, c)), c.id(), 0);

        assertEquals(List.of(c.id(), a.id(), b.id()), moved.blocks().stream().map(ScriptBlock::id).toList());
        assertEquals(List.of(0, 1, 2, 3), service.insertionZones(moved));
    }
}
