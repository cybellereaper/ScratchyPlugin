package com.github.cybellereaper.scratchy.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ScriptPaletteGui {
    public static final String TITLE = "Scratchy - Block Palette";

    private final GuiManager guiManager;
    private final EditorSession session;

    public ScriptPaletteGui(GuiManager guiManager, EditorSession session) {
        this.guiManager = guiManager;
        this.session = session;
    }

    public Inventory inventory() {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);
        inventory.setItem(10, item(Material.CHEST, "Action: Give Item"));
        inventory.setItem(11, item(Material.ZOMBIE_HEAD, "Action: Spawn Mob"));
        inventory.setItem(12, item(Material.PAPER, "Action: Message"));
        inventory.setItem(13, item(Material.NOTE_BLOCK, "Action: Sound"));
        inventory.setItem(14, item(Material.ENDER_PEARL, "Action: Teleport"));
        inventory.setItem(15, item(Material.STONE, "Action: Place Block"));
        inventory.setItem(16, item(Material.BARRIER, "Action: Remove Block"));
        inventory.setItem(19, item(Material.BEACON, "Action: Effect"));
        inventory.setItem(20, item(Material.CLOCK, "Action: Wait"));
        inventory.setItem(21, item(Material.COMMAND_BLOCK, "Action: Run Command"));
        inventory.setItem(22, item(Material.NAME_TAG, "Action: Set Variable"));
        inventory.setItem(31, item(Material.ARROW, "Back"));
        return inventory;
    }

    private ItemStack item(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return stack;
    }
}
