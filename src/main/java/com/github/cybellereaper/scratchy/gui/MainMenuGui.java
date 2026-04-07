package com.github.cybellereaper.scratchy.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MainMenuGui {
    public static final String TITLE = "Scratchy - Projects";
    private final GuiManager guiManager;

    public MainMenuGui(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    public Inventory inventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);
        inventory.setItem(11, item(Material.WRITABLE_BOOK, "Create Project"));
        inventory.setItem(13, item(Material.BOOK, "Open First Project"));
        inventory.setItem(15, item(Material.COMPARATOR, "Toggle Debug"));
        return inventory;
    }

    private ItemStack item(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of("Scratchy GUI action"));
        stack.setItemMeta(meta);
        return stack;
    }
}
