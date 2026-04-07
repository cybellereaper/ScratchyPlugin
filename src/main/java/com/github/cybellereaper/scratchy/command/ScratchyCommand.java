package com.github.cybellereaper.scratchy.command;

import com.github.cybellereaper.scratchy.domain.ProjectDefinition;
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
    private static final String PERM_USE = "scratchplugin.use";
    private static final String PERM_EDIT = "scratchplugin.edit";
    private static final String PERM_RUN = "scratchplugin.run";
    private static final String PERM_ADMIN = "scratchplugin.admin";
    private static final String PERM_DEBUG = "scratchplugin.debug";

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
        if (!sender.hasPermission(PERM_USE)) {
            sender.sendMessage("You do not have permission: " + PERM_USE);
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("open")) {
            if (!sender.hasPermission(PERM_EDIT)) {
                sender.sendMessage("You do not have permission: " + PERM_EDIT);
                return true;
            }
            if (sender instanceof Player player) {
                guiManager.openMain(player);
            } else {
                sender.sendMessage("Only players can open Scratchy GUI.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("Projects: " + projectService.listProjects().size());
            for (ProjectDefinition project : projectService.listProjects()) {
                sender.sendMessage("- " + project.name() + " / " + project.id() + " (scripts=" + project.scripts().size() + ")");
                project.scripts().forEach(script -> sender.sendMessage("  * " + script.name() + " trigger=" + script.trigger().type()));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("run")) {
            if (!sender.hasPermission(PERM_RUN)) {
                sender.sendMessage("You do not have permission: " + PERM_RUN);
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("Usage: /" + label + " run <scriptName>");
                return true;
            }
            Player player = sender instanceof Player ? (Player) sender : null;
            boolean found = runtime.runByScriptName(args[1], player);
            sender.sendMessage(found ? "Ran script '" + args[1] + "'." : "Script not found: " + args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(PERM_ADMIN)) {
                sender.sendMessage("You do not have permission: " + PERM_ADMIN);
                return true;
            }
            projectService.loadAll();
            sender.sendMessage("Scratchy project data reloaded.");
            return true;
        }

        if (args[0].equalsIgnoreCase("debug")) {
            if (!sender.hasPermission(PERM_DEBUG)) {
                sender.sendMessage("You do not have permission: " + PERM_DEBUG);
                return true;
            }
            runtime.setDebug(!runtime.debug());
            sender.sendMessage("Scratchy debug is now " + runtime.debug());
            return true;
        }

        sender.sendMessage("Usage: /" + label + " <open|list|run <script>|reload|debug>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("open", "list", "run", "reload", "debug");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("run")) {
            return projectService.listScripts().stream().map(script -> script.name()).toList();
        }
        return List.of();
    }
}
