package com.github.cybellereaper.scratchy.script.blocks;

import java.util.ArrayList;
import java.util.List;

public record ScriptSequence(List<ScriptBlock> blocks) {
    public ScriptSequence {
        blocks = blocks == null ? new ArrayList<>() : new ArrayList<>(blocks);
    }
}
