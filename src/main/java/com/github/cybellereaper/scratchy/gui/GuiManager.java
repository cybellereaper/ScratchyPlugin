package com.github.cybellereaper.scratchy.gui;

import com.github.cybellereaper.scratchy.persistence.ProjectService;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager {
    private final ProjectService projectService;
    private final Map<UUID, EditorSession> sessions = new HashMap<>();

    public GuiManager(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void openMain(Player player) {
        player.openInventory(new MainMenuGui(this).inventory());
    }

    public void openEditor(Player player, UUID projectId) {
        EditorSession session = sessions.computeIfAbsent(player.getUniqueId(), ignored -> new EditorSession(projectId));
        player.openInventory(new ScriptEditorGui(this, session).inventory());
    }

    public void openPalette(Player player, EditorSession session) {
        player.openInventory(new ScriptPaletteGui(this, session).inventory());
    }

    public void clearSession(UUID playerId) {
        sessions.remove(playerId);
    }

    public EditorSession session(UUID playerId) {
        return sessions.get(playerId);
    }

    public ProjectService projectService() {
        return projectService;
    }
}
