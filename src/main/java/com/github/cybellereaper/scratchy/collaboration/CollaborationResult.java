package com.github.cybellereaper.scratchy.collaboration;

import com.github.cybellereaper.scratchy.script.model.ScriptDocument;

public record CollaborationResult(boolean applied, long revision, ScriptDocument document, String conflictReason) {
    public static CollaborationResult applied(long revision, ScriptDocument document) {
        return new CollaborationResult(true, revision, document, "");
    }

    public static CollaborationResult conflict(long revision, ScriptDocument document, String reason) {
        return new CollaborationResult(false, revision, document, reason);
    }
}
