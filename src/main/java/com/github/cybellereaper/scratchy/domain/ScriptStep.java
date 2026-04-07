package com.github.cybellereaper.scratchy.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SequenceStep.class, name = "sequence"),
        @JsonSubTypes.Type(value = ActionStep.class, name = "action"),
        @JsonSubTypes.Type(value = IfStep.class, name = "if"),
        @JsonSubTypes.Type(value = RepeatNStep.class, name = "repeat_n"),
        @JsonSubTypes.Type(value = RepeatWhileStep.class, name = "repeat_while"),
        @JsonSubTypes.Type(value = StopStep.class, name = "stop")
})
public sealed interface ScriptStep permits SequenceStep, ActionStep, IfStep, RepeatNStep, RepeatWhileStep, StopStep {
}
