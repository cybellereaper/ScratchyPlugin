package com.github.cybellereaper.scratchy.templates;

import com.github.cybellereaper.scratchy.script.model.ScriptDocument;

public record ScriptTemplate(int schemaVersion, TemplateMetadata metadata, ScriptDocument script) {
    public static final int CURRENT_SCHEMA_VERSION = 1;
}
