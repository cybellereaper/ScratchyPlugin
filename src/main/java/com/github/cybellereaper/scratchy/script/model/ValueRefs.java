package com.github.cybellereaper.scratchy.script.model;

import com.github.cybellereaper.scratchy.script.events.EventParameterRef;
import com.github.cybellereaper.scratchy.script.variables.VariableRef;

import java.util.HashMap;
import java.util.Map;

public final class ValueRefs {
    private ValueRefs() {}

    public static Object toLiteral(ValueRef valueRef) {
        if (valueRef instanceof LiteralValueRef literal) {
            return literal.value();
        }
        if (valueRef instanceof VariableRef variableRef) {
            return "${var:" + variableRef.name() + "}";
        }
        if (valueRef instanceof EventParameterRef eventRef) {
            return "${event:" + eventRef.key() + "}";
        }
        return null;
    }

    public static Map<String, Object> toLegacyArgs(Map<String, ValueRef> refs) {
        Map<String, Object> args = new HashMap<>();
        refs.forEach((key, value) -> args.put(key, toLiteral(value)));
        return args;
    }
}
