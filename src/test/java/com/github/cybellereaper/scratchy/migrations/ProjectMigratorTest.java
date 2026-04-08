package com.github.cybellereaper.scratchy.migrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.cybellereaper.scratchy.persistence.model.PersistedProjectDocument;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectMigratorTest {
    @Test
    void migratesLegacyProjectNode() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        String legacy = """
                id: 123e4567-e89b-12d3-a456-426614174000
                name: Legacy
                scripts: []
                """;

        ProjectMigrator migrator = new ProjectMigrator(mapper, List.of(new LegacyProjectMigration(mapper)));
        PersistedProjectDocument migrated = migrator.read(mapper.readTree(legacy));

        assertEquals(PersistedProjectDocument.CURRENT_SCHEMA_VERSION, migrated.schemaVersion());
        assertEquals("Legacy", migrated.project().name());
    }
}
