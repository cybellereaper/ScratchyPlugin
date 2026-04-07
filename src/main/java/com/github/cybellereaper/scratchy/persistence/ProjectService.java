package com.github.cybellereaper.scratchy.persistence;

import com.github.cybellereaper.scratchy.domain.ProjectDefinition;
import com.github.cybellereaper.scratchy.domain.ScriptDefinition;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ProjectService {
    private final ProjectRepository repository;
    private final Logger logger;
    private final Map<UUID, ProjectDefinition> cache = new ConcurrentHashMap<>();

    public ProjectService(ProjectRepository repository, Logger logger) {
        this.repository = repository;
        this.logger = logger;
    }

    public void loadAll() {
        cache.clear();
        try {
            for (ProjectDefinition project : repository.findAll()) {
                cache.put(project.id(), project);
            }
        } catch (IOException ex) {
            logger.warning("Failed loading projects: " + ex.getMessage());
        }
    }

    public Collection<ProjectDefinition> listProjects() {
        return cache.values();
    }

    public List<ScriptDefinition> listScripts() {
        return cache.values().stream()
                .flatMap(project -> project.scripts().stream())
                .toList();
    }

    public Optional<ProjectDefinition> findProject(UUID id) {
        return Optional.ofNullable(cache.get(id));
    }

    public Optional<ScriptDefinition> findScriptByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return listScripts().stream()
                .filter(script -> script.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public ProjectDefinition createProject(String name) {
        ProjectDefinition project = new ProjectDefinition(UUID.randomUUID(), name, new ArrayList<>());
        cache.put(project.id(), project);
        persist(project);
        return project;
    }

    public boolean deleteScript(UUID scriptId) {
        for (ProjectDefinition project : cache.values()) {
            List<ScriptDefinition> scripts = new ArrayList<>(project.scripts());
            boolean removed = scripts.removeIf(existing -> existing.id().equals(scriptId));
            if (removed) {
                ProjectDefinition updated = new ProjectDefinition(project.id(), project.name(), scripts);
                cache.put(updated.id(), updated);
                persist(updated);
                return true;
            }
        }
        return false;
    }

    public void upsertScript(UUID projectId, ScriptDefinition script) {
        ProjectDefinition project = cache.get(projectId);
        if (project == null) {
            return;
        }
        List<ScriptDefinition> scripts = new ArrayList<>(project.scripts());
        scripts.removeIf(existing -> existing.id().equals(script.id()));
        scripts.add(script);
        ProjectDefinition updated = new ProjectDefinition(project.id(), project.name(), scripts);
        cache.put(project.id(), updated);
        persist(updated);
    }

    public void deleteProject(UUID projectId) {
        cache.remove(projectId);
        try {
            repository.delete(projectId);
        } catch (IOException ex) {
            logger.warning("Failed deleting project: " + ex.getMessage());
        }
    }

    private void persist(ProjectDefinition project) {
        try {
            repository.save(project);
        } catch (IOException ex) {
            logger.warning("Failed saving project " + project.id() + ": " + ex.getMessage());
        }
    }
}
