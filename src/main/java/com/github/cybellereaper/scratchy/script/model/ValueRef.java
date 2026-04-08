package com.github.cybellereaper.scratchy.script.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.cybellereaper.scratchy.script.events.EventParameterRef;
import com.github.cybellereaper.scratchy.script.variables.VariableRef;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LiteralValueRef.class, name = "literal"),
        @JsonSubTypes.Type(value = VariableRef.class, name = "variable"),
        @JsonSubTypes.Type(value = EventParameterRef.class, name = "event_param")
})
public interface ValueRef {
}
