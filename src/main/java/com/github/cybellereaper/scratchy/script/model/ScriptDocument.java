package com.github.cybellereaper.scratchy.script.model;

import com.github.cybellereaper.scratchy.domain.TriggerSpec;
import com.github.cybellereaper.scratchy.script.blocks.ScriptSequence;
import com.github.cybellereaper.scratchy.script.events.EventParameterDeclaration;
import com.github.cybellereaper.scratchy.script.variables.VariableDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ScriptDocument(UUID id,
                             String name,
                             TriggerSpec trigger,
                             ScriptSequence root,
                             List<VariableDeclaration> variables,
                             List<EventParameterDeclaration> eventParameters,
                             Map<String, Object> metadata) {
    public ScriptDocument {
        variables = variables == null ? new ArrayList<>() : new ArrayList<>(variables);
        eventParameters = eventParameters == null ? new ArrayList<>() : new ArrayList<>(eventParameters);
        metadata = metadata == null ? new HashMap<>() : new HashMap<>(metadata);
    }
}
