package com.github.cybellereaper.scratchy.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ScriptEditorGui {
    public static final String TITLE = "Scratchy - Script Editor";

    private final GuiManager guiManager;
    private final EditorSession session;

    public ScriptEditorGui(GuiManager guiManager, EditorSession session) {
        this.guiManager = guiManager;
        this.session = session;
    }

    public Inventory inventory() {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        inventory.setItem(10, item(Material.LEVER, "Trigger: " + session.trigger().type()));
        inventory.setItem(12, item(Material.GREEN_CONCRETE, "Add Block"));
        inventory.setItem(14, item(Material.REPEATER, "Add Repeat Example"));
        inventory.setItem(16, item(Material.COMPARATOR, "Add If/Else Example"));
        inventory.setItem(49, item(Material.EMERALD, "Save Script"));
        inventory.setItem(53, item(Material.BARRIER, "Delete Project"));
        return inventory;
    }

    private ItemStack item(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of("Steps in script: " + session.steps().size()));
        stack.setItemMeta(meta);
        return stack;
    }
}
