package com.github.cybellereaper.scratchy;

import com.github.cybellereaper.scratchy.command.ScratchyCommand;
import com.github.cybellereaper.scratchy.context.BukkitSchedulerFacade;
import com.github.cybellereaper.scratchy.engine.ScriptEngine;
import com.github.cybellereaper.scratchy.engine.ScriptRuntime;
import com.github.cybellereaper.scratchy.gui.GuiManager;
import com.github.cybellereaper.scratchy.listeners.GuiListener;
import com.github.cybellereaper.scratchy.listeners.TriggerListeners;
import com.github.cybellereaper.scratchy.persistence.JacksonProjectRepository;
import com.github.cybellereaper.scratchy.persistence.ProjectRepository;
import com.github.cybellereaper.scratchy.persistence.ProjectService;
import com.github.cybellereaper.scratchy.registry.ActionRegistry;
import com.github.cybellereaper.scratchy.registry.BuiltinRegistries;
import com.github.cybellereaper.scratchy.registry.ConditionRegistry;
import com.github.cybellereaper.scratchy.validation.ScriptValidator;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Scratchy extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        ProjectRepository repository = new JacksonProjectRepository(getDataFolder().toPath().resolve("projects"));
        ProjectService projectService = new ProjectService(repository, getLogger());
        projectService.loadAll();

        ActionRegistry actionRegistry = new ActionRegistry();
        ConditionRegistry conditionRegistry = new ConditionRegistry();
        BuiltinRegistries.registerDefaults(actionRegistry, conditionRegistry);

        ScriptEngine scriptEngine = new ScriptEngine(actionRegistry, conditionRegistry);
        ScriptRuntime runtime = new ScriptRuntime(this, projectService, scriptEngine, new BukkitSchedulerFacade(this), new ScriptValidator());
        runtime.setDebug(getConfig().getBoolean("debug", false));

        GuiManager guiManager = new GuiManager(projectService);

        getServer().getPluginManager().registerEvents(new GuiListener(guiManager), this);
        getServer().getPluginManager().registerEvents(new TriggerListeners(runtime), this);

        ScratchyCommand command = new ScratchyCommand(guiManager, projectService, runtime);
        registerCommand("scratchy", command);
        registerCommand("scratchplugin", command);

        long scheduledInterval = Math.max(20L, getConfig().getLong("scheduled-trigger-ticks", 200L));
        getServer().getScheduler().runTaskTimer(this, runtime::runScheduledScripts, scheduledInterval, scheduledInterval);
        getLogger().info("Scratchy enabled with " + projectService.listProjects().size() + " loaded project(s).");
    }

    private void registerCommand(String name, ScratchyCommand command) {
        PluginCommand pluginCommand = getCommand(name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }
    }
}
