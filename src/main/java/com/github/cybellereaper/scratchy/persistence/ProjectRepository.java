package com.github.cybellereaper.scratchy.persistence;

import com.github.cybellereaper.scratchy.domain.ProjectDefinition;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    void save(ProjectDefinition project) throws IOException;

    Optional<ProjectDefinition> findById(UUID id) throws IOException;

    List<ProjectDefinition> findAll() throws IOException;

    void delete(UUID id) throws IOException;
}
