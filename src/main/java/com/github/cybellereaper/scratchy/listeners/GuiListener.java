package com.github.cybellereaper.scratchy.listeners;

import com.github.cybellereaper.scratchy.domain.ProjectDefinition;
import com.github.cybellereaper.scratchy.gui.*;
import com.github.cybellereaper.scratchy.persistence.ProjectService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiListener implements Listener {
    private final GuiManager guiManager;

    public GuiListener(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || event.getCurrentItem() == null) {
            return;
        }
        String title = event.getView().getTitle();
        if (!title.startsWith("Scratchy")) {
            return;
        }
        event.setCancelled(true);

        if (MainMenuGui.TITLE.equals(title)) {
            handleMainMenu(event, player);
            return;
        }
        if (ScriptEditorGui.TITLE.equals(title)) {
            handleEditor(event, player);
            return;
        }
        if (ScriptPaletteGui.TITLE.equals(title)) {
            handlePalette(event, player);
        }
    }

    private void handleMainMenu(InventoryClickEvent event, Player player) {
        ProjectService projectService = guiManager.projectService();
        if (event.getSlot() == 11) {
            ProjectDefinition project = projectService.createProject("Project-" + System.currentTimeMillis());
            guiManager.openEditor(player, project.id());
        } else if (event.getSlot() == 13) {
            ProjectDefinition project = projectService.listProjects().stream().findFirst()
                    .orElseGet(() -> projectService.createProject("Default Project"));
            guiManager.openEditor(player, project.id());
        }
    }

    private void handleEditor(InventoryClickEvent event, Player player) {
        EditorSession session = guiManager.session(player.getUniqueId());
        if (session == null) {
            return;
        }
        switch (event.getSlot()) {
            case 10 -> {
                session.cycleTrigger();
                guiManager.openEditor(player, session.projectId());
            }
            case 12 -> guiManager.openPalette(player, session);
            case 14 -> {
                session.addRepeatExample();
                guiManager.openEditor(player, session.projectId());
            }
            case 16 -> {
                session.addIfExample();
                guiManager.openEditor(player, session.projectId());
            }
            case 49 -> {
                guiManager.projectService().upsertScript(session.projectId(), session.build());
                player.sendMessage("Saved script '" + session.name() + "'.");
            }
            case 53 -> {
                guiManager.projectService().deleteProject(session.projectId());
                player.sendMessage("Deleted project.");
                player.closeInventory();
                guiManager.clearSession(player.getUniqueId());
            }
            default -> {
            }
        }
    }

    private void handlePalette(InventoryClickEvent event, Player player) {
        EditorSession session = guiManager.session(player.getUniqueId());
        if (session == null) {
            return;
        }
        switch (event.getSlot()) {
            case 10 -> session.addAction("give_item");
            case 11 -> session.addAction("spawn_mob");
            case 12 -> session.addAction("send_message");
            case 13 -> session.addAction("play_sound");
            case 14 -> session.addAction("teleport");
            case 15 -> session.addAction("place_block");
            case 16 -> session.addAction("remove_block");
            case 19 -> session.addAction("apply_effect");
            case 20 -> session.addAction("wait");
            case 21 -> session.addAction("run_command");
            case 22 -> session.addAction("set_variable");
            case 23 -> session.addAction("increment_variable");
            case 24 -> session.addAction("stop_script");
            case 31 -> {
                guiManager.openEditor(player, session.projectId());
                return;
            }
            default -> {
                return;
            }
        }
        guiManager.openEditor(player, session.projectId());
    }
}
