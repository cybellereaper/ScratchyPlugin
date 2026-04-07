package com.github.cybellereaper.scratchy.listeners;

import com.github.cybellereaper.scratchy.domain.TriggerType;
import com.github.cybellereaper.scratchy.engine.ScriptRuntime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class TriggerListeners implements Listener {
    private final ScriptRuntime runtime;

    public TriggerListeners(ScriptRuntime runtime) {
        this.runtime = runtime;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        runtime.fire(TriggerType.JOIN, "", event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        runtime.fire(TriggerType.INTERACT, "", player);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.fire(TriggerType.BLOCK_BREAK, event.getBlock().getType().name(), event.getPlayer());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            runtime.fire(TriggerType.ENTITY_DEATH, event.getEntityType().name(), killer);
        }
    }
}
