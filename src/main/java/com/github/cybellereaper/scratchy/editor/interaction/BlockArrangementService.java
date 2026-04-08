package com.github.cybellereaper.scratchy.editor.interaction;

import com.github.cybellereaper.scratchy.script.blocks.ScriptBlock;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockArrangementService {

    public ScriptSequence move(ScriptSequence sequence, UUID blockId, int targetIndex) {
        List<ScriptBlock> blocks = new ArrayList<>(sequence.blocks());
        int fromIndex = indexOf(blocks, blockId);
        if (fromIndex < 0) {
            return sequence;
        }
        ScriptBlock moving = blocks.remove(fromIndex);
        int boundedIndex = Math.max(0, Math.min(targetIndex, blocks.size()));
        blocks.add(boundedIndex, moving);
        return new ScriptSequence(blocks);
    }

    public List<Integer> insertionZones(ScriptSequence sequence) {
        List<Integer> zones = new ArrayList<>();
        for (int i = 0; i <= sequence.blocks().size(); i++) {
            zones.add(i);
        }
        return zones;
    }

    private int indexOf(List<ScriptBlock> blocks, UUID blockId) {
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).id().equals(blockId)) {
                return i;
            }
        }
        return -1;
    }
}
