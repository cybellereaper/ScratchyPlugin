package com.github.cybellereaper.scratchy.collaboration;

import com.github.cybellereaper.scratchy.script.blocks.ScriptBlock;

public record AddBlockOperation(long baseRevision, ScriptBlock block, int targetIndex) implements CollaborationOperation {
}
