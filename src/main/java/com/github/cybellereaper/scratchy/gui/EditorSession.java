package com.github.cybellereaper.scratchy.gui;

import com.github.cybellereaper.scratchy.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EditorSession {
    private final UUID projectId;
    private final UUID scriptId;
    private String name;
    private TriggerSpec triggerSpec = TriggerSpec.command("scratchy-example");
    private final List<ScriptStep> steps = new ArrayList<>();

    public EditorSession(UUID projectId) {
        this(projectId, UUID.randomUUID(), "New Script");
    }

    public EditorSession(UUID projectId, UUID scriptId, String name) {
        this.projectId = projectId;
        this.scriptId = scriptId;
        this.name = name;
    }

    public UUID projectId() {
        return projectId;
    }

    public UUID scriptId() {
        return scriptId;
    }

    public String name() {
        return name;
    }

    public void cycleTrigger() {
        TriggerType next = switch (triggerSpec.type()) {
            case COMMAND -> TriggerType.JOIN;
            case JOIN -> TriggerType.INTERACT;
            case INTERACT -> TriggerType.BLOCK_BREAK;
            case BLOCK_BREAK -> TriggerType.ENTITY_DEATH;
            case ENTITY_DEATH -> TriggerType.SCHEDULED;
            case SCHEDULED -> TriggerType.COMMAND;
        };
        triggerSpec = new TriggerSpec(next, "scratchy-example", 200L);
    }

    public TriggerSpec trigger() {
        return triggerSpec;
    }

    public List<ScriptStep> steps() {
        return steps;
    }

    public void addAction(String type) {
        steps.add(new ActionStep(type, Map.of()));
    }

    public void addIfExample() {
        ConditionSpec condition = new ConditionSpec("random_chance", Map.of("chance", 0.5));
        ScriptStep thenStep = new ActionStep("send_message", Map.of("message", "&aCondition true"));
        ScriptStep elseStep = new ActionStep("send_message", Map.of("message", "&cCondition false"));
        steps.add(new IfStep(condition, thenStep, elseStep));
    }

    public void addRepeatExample() {
        steps.add(new RepeatNStep(3, new ActionStep("play_sound", Map.of("sound", "ENTITY_EXPERIENCE_ORB_PICKUP"))));
    }

    public ScriptDefinition build() {
        return new ScriptDefinition(scriptId, name, triggerSpec, new SequenceStep(steps));
    }
}
