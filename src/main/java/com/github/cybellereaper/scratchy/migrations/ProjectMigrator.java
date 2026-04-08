package com.github.cybellereaper.scratchy.migrations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cybellereaper.scratchy.persistence.model.PersistedProjectDocument;

import java.util.List;

public class ProjectMigrator {
    private final ObjectMapper mapper;
    private final List<ProjectMigration> migrations;

    public ProjectMigrator(ObjectMapper mapper, List<ProjectMigration> migrations) {
        this.mapper = mapper;
        this.migrations = migrations;
    }

    public PersistedProjectDocument read(JsonNode rawNode) {
        if (rawNode.has("schemaVersion") && rawNode.has("project")) {
            PersistedProjectDocument doc = mapper.convertValue(rawNode, PersistedProjectDocument.class);
            if (doc.schemaVersion() > PersistedProjectDocument.CURRENT_SCHEMA_VERSION) {
                throw new IllegalArgumentException("Unsupported schema version: " + doc.schemaVersion());
            }
            return doc;
        }
        return migrations.stream()
                .filter(migration -> migration.supports(rawNode))
                .findFirst()
                .map(migration -> migration.migrate(rawNode))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported project format"));
    }
}
