package com.github.cybellereaper.scratchy.command;

import com.github.cybellereaper.scratchy.domain.TriggerType;
import com.github.cybellereaper.scratchy.engine.ScriptRuntime;
import com.github.cybellereaper.scratchy.gui.GuiManager;
import com.github.cybellereaper.scratchy.persistence.ProjectService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class ScratchyCommand implements CommandExecutor, TabCompleter {
    private final GuiManager guiManager;
    private final ProjectService projectService;
    private final ScriptRuntime runtime;

    public ScratchyCommand(GuiManager guiManager, ProjectService projectService, ScriptRuntime runtime) {
        this.guiManager = guiManager;
        this.projectService = projectService;
        this.runtime = runtime;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("open")) {
            if (sender instanceof Player player) {
                guiManager.openMain(player);
            } else {
                sender.sendMessage("Only players can open Scratchy GUI.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("Projects: " + projectService.listProjects().size());
            projectService.listProjects().forEach(project -> sender.sendMessage("- " + project.name() + " / " + project.id()));
            return true;
        }

        if (args[0].equalsIgnoreCase("debug")) {
            runtime.setDebug(!runtime.debug());
            sender.sendMessage("Scratchy debug is now " + runtime.debug());
            return true;
        }

        if (args[0].equalsIgnoreCase("trigger") && sender instanceof Player player && args.length > 1) {
            runtime.fire(TriggerType.COMMAND, args[1], player);
            sender.sendMessage("Triggered command scripts for key: " + args[1]);
            return true;
        }

        sender.sendMessage("Usage: /scratchy <open|list|debug|trigger <key>>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("open", "list", "debug", "trigger");
        }
        return List.of();
    }
}
