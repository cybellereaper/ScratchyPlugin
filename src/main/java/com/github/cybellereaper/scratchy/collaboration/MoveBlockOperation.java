package com.github.cybellereaper.scratchy.collaboration;

import java.util.UUID;

public record MoveBlockOperation(long baseRevision, UUID blockId, int targetIndex) implements CollaborationOperation {
}
