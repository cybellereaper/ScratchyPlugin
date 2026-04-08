package com.github.cybellereaper.scratchy.migrations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cybellereaper.scratchy.domain.ProjectDefinition;
import com.github.cybellereaper.scratchy.persistence.model.PersistedProjectDocument;

public class LegacyProjectMigration implements ProjectMigration {
    private final ObjectMapper mapper;

    public LegacyProjectMigration(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean supports(JsonNode rawNode) {
        return rawNode.has("id") && rawNode.has("scripts") && !rawNode.has("schemaVersion");
    }

    @Override
    public PersistedProjectDocument migrate(JsonNode rawNode) {
        ProjectDefinition project = mapper.convertValue(rawNode, ProjectDefinition.class);
        return new PersistedProjectDocument(PersistedProjectDocument.CURRENT_SCHEMA_VERSION, project);
    }
}
