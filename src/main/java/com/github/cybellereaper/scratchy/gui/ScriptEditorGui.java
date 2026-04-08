package com.github.cybellereaper.scratchy.gui;

import com.github.cybellereaper.scratchy.script.blocks.ScriptBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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

        List<ScriptBlock> blocks = session.blocks();
        int slot = 27;
        for (int index = 0; index < blocks.size() && slot <= 44; index++, slot++) {
            ScriptBlock block = blocks.get(index);
            boolean dragging = block.id().equals(session.draggingBlockId());
            inventory.setItem(slot, scriptBlockItem(block, index, dragging));
        }

        return inventory;
    }

    private ItemStack scriptBlockItem(ScriptBlock block, int index, boolean dragging) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName((dragging ? "[Dragging] " : "") + "#" + (index + 1) + " " + block.blockType());
        List<String> lore = new ArrayList<>();
        lore.add("Left click: pick up / drop block");
        lore.add("Use drag-drop to reorder root sequence");
        lore.add("Branches: " + block.branches().keySet());
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack item(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of("Blocks in script: " + session.blocks().size()));
        stack.setItemMeta(meta);
        return stack;
    }
}
