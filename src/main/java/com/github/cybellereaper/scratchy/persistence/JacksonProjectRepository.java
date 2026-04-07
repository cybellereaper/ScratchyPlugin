package com.github.cybellereaper.scratchy.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.cybellereaper.scratchy.domain.ProjectDefinition;

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

    public JacksonProjectRepository(Path baseDir) {
        this.baseDir = baseDir;
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.mapper.findAndRegisterModules();
    }

    @Override
    public void save(ProjectDefinition project) throws IOException {
        Files.createDirectories(baseDir);
        mapper.writeValue(file(project.id()).toFile(), project);
    }

    @Override
    public Optional<ProjectDefinition> findById(UUID id) throws IOException {
        Path file = file(id);
        if (Files.notExists(file)) {
            return Optional.empty();
        }
        return Optional.of(mapper.readValue(file.toFile(), ProjectDefinition.class));
    }

    @Override
    public List<ProjectDefinition> findAll() throws IOException {
        Files.createDirectories(baseDir);
        List<ProjectDefinition> projects = new ArrayList<>();
        try (var stream = Files.list(baseDir)) {
            stream.filter(path -> path.getFileName().toString().endsWith(".yml"))
                    .forEach(path -> {
                        try {
                            projects.add(mapper.readValue(path.toFile(), ProjectDefinition.class));
                        } catch (IOException ignored) {
                        }
                    });
        }
        return projects;
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.deleteIfExists(file(id));
    }

    private Path file(UUID id) {
        return baseDir.resolve(id + ".yml");
    }
}
