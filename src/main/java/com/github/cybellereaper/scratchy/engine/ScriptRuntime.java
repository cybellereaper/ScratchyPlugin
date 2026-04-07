package com.github.cybellereaper.scratchy.engine;

import com.github.cybellereaper.scratchy.context.BukkitScriptExecutionContext;
import com.github.cybellereaper.scratchy.domain.ProjectDefinition;
import com.github.cybellereaper.scratchy.domain.ScriptDefinition;
import com.github.cybellereaper.scratchy.domain.TriggerType;
import com.github.cybellereaper.scratchy.persistence.ProjectService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class ScriptRuntime {
    private final JavaPlugin plugin;
    private final ProjectService projectService;
    private final ScriptEngine engine;
    private final SchedulerFacade scheduler;
    private boolean debug;

    public ScriptRuntime(JavaPlugin plugin, ProjectService projectService, ScriptEngine engine, SchedulerFacade scheduler) {
        this.plugin = plugin;
        this.projectService = projectService;
        this.engine = engine;
        this.scheduler = scheduler;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean debug() {
        return debug;
    }

    public void fire(TriggerType triggerType, String value, Player player) {
        matchingScripts(triggerType, value)
                .forEach(script -> execute(script, player));
    }

    public void runScheduledScripts() {
        matchingScripts(TriggerType.SCHEDULED, "")
                .forEach(script -> execute(script, null));
    }

    public List<ScriptDefinition> matchingScripts(TriggerType triggerType, String value) {
        String normalizedValue = value == null ? "" : value.toLowerCase(Locale.ROOT);
        List<ScriptDefinition> scripts = new ArrayList<>();
        for (ProjectDefinition project : projectService.listProjects()) {
            Stream<ScriptDefinition> stream = project.scripts().stream()
                    .filter(script -> script.trigger().type() == triggerType);
            if (triggerType == TriggerType.COMMAND) {
                stream = stream.filter(script -> script.trigger().value().equalsIgnoreCase(normalizedValue));
            }
            scripts.addAll(stream.toList());
        }
        return scripts;
    }

    private void execute(ScriptDefinition script, Player player) {
        if (debug) {
            plugin.getLogger().info("Executing script: " + script.name() + " [" + script.id() + "]");
        }
        engine.execute(script, new BukkitScriptExecutionContext(player, plugin.getLogger(), scheduler));
    }
}
