package com.github.cybellereaper.scratchy.script.variables;

import com.github.cybellereaper.scratchy.script.events.EventParameterDeclaration;
import com.github.cybellereaper.scratchy.script.model.ScriptDocument;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class VariableScopeResolver {

    public Optional<String> validate(ScriptDocument document) {
        Set<String> names = new HashSet<>();
        for (VariableDeclaration declaration : document.variables()) {
            String key = scopedKey(declaration.scope(), declaration.name());
            if (!names.add(key)) {
                return Optional.of("Duplicate variable declaration in scope: " + declaration.scope() + "/" + declaration.name());
            }
        }

        Set<String> eventKeys = new HashSet<>();
        for (EventParameterDeclaration eventParameter : document.eventParameters()) {
            if (!eventKeys.add(eventParameter.key())) {
                return Optional.of("Duplicate event parameter: " + eventParameter.key());
            }
        }
        return Optional.empty();
    }

    private String scopedKey(VariableScope scope, String name) {
        return scope.name() + ":" + name;
    }
}
