package com.github.cybellereaper.scratchy.domain;

import java.util.ArrayList;
import java.util.List;

public record SequenceStep(List<ScriptStep> steps) implements ScriptStep {
    public SequenceStep {
        steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps);
    }
}
