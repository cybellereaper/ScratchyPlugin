package com.github.cybellereaper.scratchy.persistence.model;

import com.github.cybellereaper.scratchy.domain.ProjectDefinition;

public record PersistedProjectDocument(int schemaVersion, ProjectDefinition project) {
    public static final int CURRENT_SCHEMA_VERSION = 2;
}
