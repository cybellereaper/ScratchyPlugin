package com.github.cybellereaper.scratchy.collaboration;

import com.github.cybellereaper.scratchy.editor.interaction.BlockArrangementService;
import com.github.cybellereaper.scratchy.script.blocks.ScriptBlock;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;
import com.github.cybellereaper.scratchy.script.model.ScriptDocument;

import java.util.ArrayList;
import java.util.List;

public class CollaborativeScriptSession {
    private final BlockArrangementService arrangementService = new BlockArrangementService();
    private ScriptDocument document;
    private long revision;

    public CollaborativeScriptSession(ScriptDocument document) {
        this.document = document;
        this.revision = 0;
    }

    public CollaborationResult apply(CollaborationOperation operation) {
        if (operation.baseRevision() != revision) {
            return CollaborationResult.conflict(revision, document, "Stale revision");
        }

        if (operation instanceof MoveBlockOperation move) {
            ScriptSequence moved = arrangementService.move(document.root(), move.blockId(), move.targetIndex());
            document = new ScriptDocument(document.id(), document.name(), document.trigger(), moved,
                    document.variables(), document.eventParameters(), document.metadata());
        } else if (operation instanceof AddBlockOperation add) {
            List<ScriptBlock> blocks = new ArrayList<>(document.root().blocks());
            int index = Math.max(0, Math.min(add.targetIndex(), blocks.size()));
            blocks.add(index, add.block());
            document = new ScriptDocument(document.id(), document.name(), document.trigger(), new ScriptSequence(blocks),
                    document.variables(), document.eventParameters(), document.metadata());
        }

        revision++;
        return CollaborationResult.applied(revision, document);
    }

    public long revision() {
        return revision;
    }

    public ScriptDocument document() {
        return document;
    }
}
