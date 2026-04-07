package com.github.cybellereaper.scratchy.validation;

import com.github.cybellereaper.scratchy.domain.*;

import java.util.ArrayList;
import java.util.List;

public class ScriptValidator {

    public ValidationResult validate(ScriptDefinition script) {
        List<String> errors = new ArrayList<>();
        if (script == null) {
            return ValidationResult.fromErrors(List.of("Script cannot be null"));
        }
        if (script.name() == null || script.name().isBlank()) {
            errors.add("Script name cannot be blank");
        }
        if (script.trigger() == null) {
            errors.add("Trigger is required");
        } else {
            validateTrigger(script.trigger(), errors);
        }
        if (script.root() == null) {
            errors.add("Script root node is required");
        } else {
            validateStep(script.root(), errors);
        }
        return ValidationResult.fromErrors(errors);
    }

    private void validateTrigger(TriggerSpec trigger, List<String> errors) {
        if (trigger.type() == TriggerType.COMMAND && (trigger.value() == null || trigger.value().isBlank())) {
            errors.add("Command trigger requires a non-empty command key");
        }
        if (trigger.type() == TriggerType.SCHEDULED && trigger.intervalTicks() < 20L) {
            errors.add("Scheduled trigger interval must be at least 20 ticks");
        }
    }

    private void validateStep(ScriptStep step, List<String> errors) {
        if (step instanceof ActionStep actionStep) {
            if (actionStep.actionType() == null || actionStep.actionType().isBlank()) {
                errors.add("Action type cannot be blank");
            }
            return;
        }
        if (step instanceof SequenceStep sequenceStep) {
            if (sequenceStep.steps().isEmpty()) {
                errors.add("Sequence cannot be empty");
            }
            sequenceStep.steps().forEach(child -> validateStep(child, errors));
            return;
        }
        if (step instanceof IfStep ifStep) {
            if (ifStep.condition() == null || ifStep.condition().type() == null || ifStep.condition().type().isBlank()) {
                errors.add("If condition is required");
            }
            if (ifStep.thenStep() == null) {
                errors.add("If then-step is required");
            } else {
                validateStep(ifStep.thenStep(), errors);
            }
            if (ifStep.elseStep() != null) {
                validateStep(ifStep.elseStep(), errors);
            }
            return;
        }
        if (step instanceof RepeatNStep repeatNStep) {
            if (repeatNStep.times() <= 0) {
                errors.add("Repeat N requires times > 0");
            }
            if (repeatNStep.body() == null) {
                errors.add("Repeat N requires a body");
            } else {
                validateStep(repeatNStep.body(), errors);
            }
            return;
        }
        if (step instanceof RepeatWhileStep repeatWhileStep) {
            if (repeatWhileStep.condition() == null || repeatWhileStep.condition().type() == null || repeatWhileStep.condition().type().isBlank()) {
                errors.add("Repeat while requires a condition");
            }
            if (repeatWhileStep.body() == null) {
                errors.add("Repeat while requires a body");
            } else {
                validateStep(repeatWhileStep.body(), errors);
            }
            if (repeatWhileStep.maxIterations() <= 0) {
                errors.add("Repeat while maxIterations must be > 0");
            }
        }
    }
}
