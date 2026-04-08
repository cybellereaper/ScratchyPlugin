package com.github.cybellereaper.scratchy.script.variables;

import com.github.cybellereaper.scratchy.script.model.ValueRef;

public record VariableDeclaration(String name, VariableType type, VariableScope scope, ValueRef defaultValue) {
}
