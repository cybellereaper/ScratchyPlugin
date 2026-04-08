package com.github.cybellereaper.scratchy.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.cybellereaper.scratchy.domain.ProjectDefinition;
import com.github.cybellereaper.scratchy.migrations.LegacyProjectMigration;
import com.github.cybellereaper.scratchy.migrations.ProjectMigrator;
import com.github.cybellereaper.scratchy.persistence.model.PersistedProjectDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JacksonProjectRepository implements ProjectRepository {
    private final Path baseDir;
    private final ObjectMapper mapper;
    private final ProjectMigrator migrator;

    public JacksonProjectRepository(Path baseDir) {
        this.baseDir = baseDir;
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.mapper.findAndRegisterModules();
        this.migrator = new ProjectMigrator(mapper, List.of(new LegacyProjectMigration(mapper)));
    }

    @Override
    public void save(ProjectDefinition project) throws IOException {
        Files.createDirectories(baseDir);
        PersistedProjectDocument persisted = new PersistedProjectDocument(PersistedProjectDocument.CURRENT_SCHEMA_VERSION, project);
        mapper.writeValue(file(project.id()).toFile(), persisted);
    }

    @Override
    public Optional<ProjectDefinition> findById(UUID id) throws IOException {
        Path file = file(id);
        if (Files.notExists(file)) {
            return Optional.empty();
        }
        return Optional.of(readProject(file));
    }

    @Override
    public List<ProjectDefinition> findAll() throws IOException {
        Files.createDirectories(baseDir);
        List<ProjectDefinition> projects = new ArrayList<>();
        try (var stream = Files.list(baseDir)) {
            stream.filter(path -> path.getFileName().toString().endsWith(".yml"))
                    .forEach(path -> {
                        try {
                            projects.add(readProject(path));
                        } catch (IOException ignored) {
                        }
                    });
        }
        return projects;
    }

    private ProjectDefinition readProject(Path path) throws IOException {
        JsonNode rawNode = mapper.readTree(path.toFile());
        return migrator.read(rawNode).project();
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.deleteIfExists(file(id));
    }

    private Path file(UUID id) {
        return baseDir.resolve(id + ".yml");
    }
}
