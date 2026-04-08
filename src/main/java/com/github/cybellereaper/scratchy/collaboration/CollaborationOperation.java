package com.github.cybellereaper.scratchy.collaboration;

public sealed interface CollaborationOperation permits MoveBlockOperation, AddBlockOperation {
    long baseRevision();
}
