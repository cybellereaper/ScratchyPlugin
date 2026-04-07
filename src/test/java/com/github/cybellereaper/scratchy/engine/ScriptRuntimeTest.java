package com.github.cybellereaper.scratchy.engine;

import com.github.cybellereaper.scratchy.domain.*;
import com.github.cybellereaper.scratchy.persistence.ProjectRepository;
import com.github.cybellereaper.scratchy.persistence.ProjectService;
import com.github.cybellereaper.scratchy.registry.ActionRegistry;
import com.github.cybellereaper.scratchy.registry.ConditionRegistry;
import com.github.cybellereaper.scratchy.validation.ScriptValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ScriptRuntimeTest {

    @Test
    void runByScriptNameReturnsFalseWhenMissing() {
        ProjectService service = new ProjectService(new InMemoryRepo(), java.util.logging.Logger.getLogger("test"));
        ScriptRuntime runtime = new ScriptRuntime(
                Mockito.mock(org.bukkit.plugin.java.JavaPlugin.class),
                service,
                new ScriptEngine(new ActionRegistry(), new ConditionRegistry()),
                (ticks, runnable) -> runnable.run(),
                new ScriptValidator()
        );

        assertFalse(runtime.runByScriptName("missing", null));
    }

    @Test
    void matchingScriptsFiltersCommandKey() {
        InMemoryRepo repo = new InMemoryRepo();
        ProjectService service = new ProjectService(repo, java.util.logging.Logger.getLogger("test"));
        UUID projectId = UUID.randomUUID();
        repo.saveUnchecked(new ProjectDefinition(projectId, "p", List.of(
                new ScriptDefinition(UUID.randomUUID(), "a", TriggerSpec.command("hello"), new ActionStep("noop", Map.of())),
                new ScriptDefinition(UUID.randomUUID(), "b", TriggerSpec.command("other"), new ActionStep("noop", Map.of()))
        )));
        service.loadAll();

        ActionRegistry actions = new ActionRegistry();
        actions.register("noop", (step, ctx) -> CompletableFuture.completedFuture(ExecutionSignal.CONTINUE));
        ScriptRuntime runtime = new ScriptRuntime(
                Mockito.mock(org.bukkit.plugin.java.JavaPlugin.class),
                service,
                new ScriptEngine(actions, new ConditionRegistry()),
                (ticks, runnable) -> runnable.run(),
                new ScriptValidator()
        );

        List<ScriptDefinition> matches = runtime.matchingScripts(TriggerType.COMMAND, "hello");
        assertEquals(1, matches.size());
        assertEquals("a", matches.getFirst().name());
    }

    private static class InMemoryRepo implements ProjectRepository {
        private ProjectDefinition project;

        @Override
        public void save(ProjectDefinition project) {
            this.project = project;
        }

        void saveUnchecked(ProjectDefinition project) {
            this.project = project;
        }

        @Override
        public Optional<ProjectDefinition> findById(UUID id) {
            return Optional.ofNullable(project).filter(p -> p.id().equals(id));
        }

        @Override
        public List<ProjectDefinition> findAll() {
            return project == null ? List.of() : List.of(project);
        }

        @Override
        public void delete(UUID id) {
            this.project = null;
        }
    }
}
