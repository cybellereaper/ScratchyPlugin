package com.github.cybellereaper.scratchy.migrations;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.cybellereaper.scratchy.persistence.model.PersistedProjectDocument;

public interface ProjectMigration {
    boolean supports(JsonNode rawNode);

    PersistedProjectDocument migrate(JsonNode rawNode);
}
