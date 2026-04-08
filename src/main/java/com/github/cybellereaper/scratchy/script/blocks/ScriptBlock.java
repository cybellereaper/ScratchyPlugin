package com.github.cybellereaper.scratchy.script.blocks;

import com.github.cybellereaper.scratchy.script.model.ValueRef;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ScriptBlock(UUID id,
                          String blockType,
                          Map<String, ValueRef> args,
                          Map<String, ScriptSequence> branches,
                          Map<String, Object> metadata) {
    public ScriptBlock {
        id = id == null ? UUID.randomUUID() : id;
        args = args == null ? new HashMap<>() : new HashMap<>(args);
        branches = branches == null ? new HashMap<>() : new HashMap<>(branches);
        metadata = metadata == null ? new HashMap<>() : new HashMap<>(metadata);
    }
}
