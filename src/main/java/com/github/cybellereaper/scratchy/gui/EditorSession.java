package com.github.cybellereaper.scratchy.gui;

import com.github.cybellereaper.scratchy.domain.ScriptDefinition;
import com.github.cybellereaper.scratchy.domain.TriggerSpec;
import com.github.cybellereaper.scratchy.domain.TriggerType;
import com.github.cybellereaper.scratchy.editor.interaction.BlockArrangementService;
import com.github.cybellereaper.scratchy.script.blocks.ScriptBlock;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;
import com.github.cybellereaper.scratchy.script.events.EventParameterDeclaration;
import com.github.cybellereaper.scratchy.script.events.EventParameterType;
import com.github.cybellereaper.scratchy.script.model.LiteralValueRef;
import com.github.cybellereaper.scratchy.script.model.ScriptDocument;
import com.github.cybellereaper.scratchy.script.model.ScriptDocumentCompiler;
import com.github.cybellereaper.scratchy.script.model.ValueRef;
import com.github.cybellereaper.scratchy.script.variables.VariableDeclaration;
import com.github.cybellereaper.scratchy.script.variables.VariableScope;
import com.github.cybellereaper.scratchy.script.variables.VariableType;

import java.util.*;

public class EditorSession {
    private final UUID projectId;
    private final BlockArrangementService arrangementService = new BlockArrangementService();
    private final ScriptDocumentCompiler compiler = new ScriptDocumentCompiler();
    private ScriptDocument document;
    private UUID draggingBlockId;

    public EditorSession(UUID projectId) {
        this(projectId, UUID.randomUUID(), "New Script");
    }

    public EditorSession(UUID projectId, UUID scriptId, String name) {
        this.projectId = projectId;
        this.document = new ScriptDocument(
                scriptId,
                name,
                TriggerSpec.command("scratchy-example"),
                new ScriptSequence(List.of()),
                new ArrayList<>(List.of(new VariableDeclaration("counter", VariableType.NUMBER, VariableScope.SCRIPT, new LiteralValueRef(0)))),
                new ArrayList<>(List.of(
                        new EventParameterDeclaration("player", EventParameterType.PLAYER, true),
                        new EventParameterDeclaration("target_block", EventParameterType.BLOCK, false)
                )),
                new HashMap<>()
        );
    }

    public UUID projectId() { return projectId; }
    public UUID scriptId() { return document.id(); }
    public String name() { return document.name(); }

    public void cycleTrigger() {
        TriggerType next = switch (document.trigger().type()) {
            case COMMAND -> TriggerType.JOIN;
            case JOIN -> TriggerType.INTERACT;
            case INTERACT -> TriggerType.BLOCK_BREAK;
            case BLOCK_BREAK -> TriggerType.ENTITY_DEATH;
            case ENTITY_DEATH -> TriggerType.SCHEDULED;
            case SCHEDULED -> TriggerType.COMMAND;
        };
        replaceDocument(new ScriptDocument(document.id(), document.name(), new TriggerSpec(next, "scratchy-example", 200L), document.root(),
                document.variables(), document.eventParameters(), document.metadata()));
    }

    public TriggerSpec trigger() { return document.trigger(); }

    public List<ScriptBlock> blocks() {
        return document.root().blocks();
    }

    public UUID draggingBlockId() {
        return draggingBlockId;
    }

    public void startDragging(UUID blockId) {
        this.draggingBlockId = blockId;
    }

    public void dropAt(int index) {
        if (draggingBlockId == null) {
            return;
        }
        ScriptSequence moved = arrangementService.move(document.root(), draggingBlockId, index);
        replaceDocument(new ScriptDocument(document.id(), document.name(), document.trigger(), moved,
                document.variables(), document.eventParameters(), document.metadata()));
        draggingBlockId = null;
    }

    public void addAction(String type) {
        Map<String, ValueRef> args = new HashMap<>();
        args.put("message", new LiteralValueRef("Hello"));
        addBlock(new ScriptBlock(UUID.randomUUID(), type, args, Map.of(), Map.of()));
    }

    public void addIfExample() {
        ScriptBlock thenMessage = new ScriptBlock(UUID.randomUUID(), "send_message",
                Map.of("message", new LiteralValueRef("&aCondition true")), Map.of(), Map.of());
        ScriptBlock elseMessage = new ScriptBlock(UUID.randomUUID(), "send_message",
                Map.of("message", new LiteralValueRef("&cCondition false")), Map.of(), Map.of());

        ScriptBlock ifBlock = new ScriptBlock(
                UUID.randomUUID(),
                "if",
                Map.of("condition", new LiteralValueRef("random_chance")),
                Map.of(
                        "then", new ScriptSequence(List.of(thenMessage)),
                        "else", new ScriptSequence(List.of(elseMessage))
                ),
                Map.of("display", "if/else")
        );
        addBlock(ifBlock);
    }

    public void addRepeatExample() {
        ScriptBlock body = new ScriptBlock(UUID.randomUUID(), "play_sound", Map.of("sound", new LiteralValueRef("ENTITY_EXPERIENCE_ORB_PICKUP")), Map.of(), Map.of());
        ScriptBlock repeat = new ScriptBlock(UUID.randomUUID(), "repeat_n", Map.of("times", new LiteralValueRef(3)), Map.of("body", new ScriptSequence(List.of(body))), Map.of());
        addBlock(repeat);
    }

    public ScriptDefinition build() {
        return compiler.compile(document);
    }

    private void addBlock(ScriptBlock block) {
        List<ScriptBlock> blocks = new ArrayList<>(document.root().blocks());
        blocks.add(block);
        replaceDocument(new ScriptDocument(document.id(), document.name(), document.trigger(), new ScriptSequence(blocks),
                document.variables(), document.eventParameters(), document.metadata()));
    }

    private void replaceDocument(ScriptDocument next) {
        this.document = next;
    }
}
