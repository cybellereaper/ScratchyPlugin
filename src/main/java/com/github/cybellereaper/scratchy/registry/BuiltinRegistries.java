package com.github.cybellereaper.scratchy.registry;

import com.github.cybellereaper.scratchy.domain.ActionStep;
import com.github.cybellereaper.scratchy.domain.ConditionSpec;
import com.github.cybellereaper.scratchy.engine.ExecutionSignal;
import com.github.cybellereaper.scratchy.engine.ScriptExecutionContext;
import com.github.cybellereaper.scratchy.util.Args;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class BuiltinRegistries {
    private BuiltinRegistries() {
    }

    public static void registerDefaults(ActionRegistry actions, ConditionRegistry conditions) {
        registerActions(actions);
        registerConditions(conditions);
    }

    private static void registerActions(ActionRegistry actions) {
        actions.register("give_item", BuiltinRegistries::giveItem);
        actions.register("spawn_mob", BuiltinRegistries::spawnMob);
        actions.register("send_message", BuiltinRegistries::sendMessage);
        actions.register("play_sound", BuiltinRegistries::playSound);
        actions.register("teleport", BuiltinRegistries::teleport);
        actions.register("place_block", BuiltinRegistries::placeBlock);
        actions.register("remove_block", BuiltinRegistries::removeBlock);
        actions.register("apply_effect", BuiltinRegistries::applyEffect);
        actions.register("wait", BuiltinRegistries::waitAction);
        actions.register("run_command", BuiltinRegistries::runCommand);
        actions.register("set_variable", BuiltinRegistries::setVariable);
    }

    private static void registerConditions(ConditionRegistry conditions) {
        conditions.register("has_item", BuiltinRegistries::hasItem);
        conditions.register("nearby_entity", BuiltinRegistries::nearbyEntity);
        conditions.register("random_chance", BuiltinRegistries::randomChance);
        conditions.register("block_match", BuiltinRegistries::blockMatch);
        conditions.register("health_threshold", BuiltinRegistries::healthThreshold);
    }

    private static CompletableFuture<ExecutionSignal> giveItem(ActionStep step, ScriptExecutionContext context) {
        return withPlayer(context, player -> {
            Material material = Material.matchMaterial(Args.str(step.args(), "material", "STONE"));
            if (material == null) {
                return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
            }
            int amount = Math.max(1, Args.integer(step.args(), "amount", 1));
            player.getInventory().addItem(new ItemStack(material, amount));
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });
    }

    private static CompletableFuture<ExecutionSignal> spawnMob(ActionStep step, ScriptExecutionContext context) {
        return withPlayer(context, player -> {
            EntityType type = EntityType.fromName(Args.str(step.args(), "entity", "ZOMBIE"));
            if (type != null) {
                int count = Math.max(1, Args.integer(step.args(), "count", 1));
                for (int i = 0; i < count; i++) {
                    player.getWorld().spawnEntity(player.getLocation(), type);
                }
            }
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });
    }

    private static CompletableFuture<ExecutionSignal> sendMessage(ActionStep step, ScriptExecutionContext context) {
        return withPlayer(context, player -> {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Args.str(step.args(), "message", "Hello!")));
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });
    }

    private static CompletableFuture<ExecutionSignal> playSound(ActionStep step, ScriptExecutionContext context) {
        return withPlayer(context, player -> {
            Sound sound;
            try {
                sound = Sound.valueOf(Args.str(step.args(), "sound", "ENTITY_EXPERIENCE_ORB_PICKUP").toUpperCase());
            } catch (IllegalArgumentException ex) {
                return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
            }
            float volume = (float) Args.decimal(step.args(), "volume", 1.0);
            float pitch = (float) Args.decimal(step.args(), "pitch", 1.0);
            player.playSound(player.getLocation(), sound, volume, pitch);
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });
    }

    private static CompletableFuture<ExecutionSignal> teleport(ActionStep step, ScriptExecutionContext context) {
        return withPlayer(context, player -> {
            World world = Bukkit.getWorld(Args.str(step.args(), "world", player.getWorld().getName()));
            if (world == null) {
                return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
            }
            double x = Args.decimal(step.args(), "x", player.getLocation().getX());
            double y = Args.decimal(step.args(), "y", player.getLocation().getY());
            double z = Args.decimal(step.args(), "z", player.getLocation().getZ());
            player.teleport(new Location(world, x, y, z));
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });
    }

    private static CompletableFuture<ExecutionSignal> placeBlock(ActionStep step, ScriptExecutionContext context) {
        return withPlayer(context, player -> {
            World world = Bukkit.getWorld(Args.str(step.args(), "world", player.getWorld().getName()));
            if (world == null) {
                return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
            }
            Material material = Material.matchMaterial(Args.str(step.args(), "material", "STONE"));
            if (material == null || !material.isBlock()) {
                return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
            }
            int x = Args.integer(step.args(), "x", player.getLocation().getBlockX());
            int y = Args.integer(step.args(), "y", player.getLocation().getBlockY());
            int z = Args.integer(step.args(), "z", player.getLocation().getBlockZ());
            world.getBlockAt(x, y, z).setType(material);
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });
    }

    private static CompletableFuture<ExecutionSignal> removeBlock(ActionStep step, ScriptExecutionContext context) {
        ActionStep placeLike = new ActionStep("remove_block", Map.of(
                "material", "AIR",
                "world", step.args().getOrDefault("world", ""),
                "x", step.args().getOrDefault("x", 0),
                "y", step.args().getOrDefault("y", 0),
                "z", step.args().getOrDefault("z", 0)
        ));
        return placeBlock(placeLike, context);
    }

    private static CompletableFuture<ExecutionSignal> applyEffect(ActionStep step, ScriptExecutionContext context) {
        return withPlayer(context, player -> {
            String effectName = Args.str(step.args(), "effect", "SPEED").toUpperCase();
            PotionEffectType effectType = PotionEffectType.getByName(effectName);
            if (effectType == null) {
                return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
            }
            int duration = Math.max(1, Args.integer(step.args(), "duration", 100));
            int amplifier = Math.max(0, Args.integer(step.args(), "amplifier", 0));
            player.addPotionEffect(new PotionEffect(effectType, duration, amplifier));
            return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
        });
    }

    private static CompletableFuture<ExecutionSignal> waitAction(ActionStep step, ScriptExecutionContext context) {
        CompletableFuture<ExecutionSignal> future = new CompletableFuture<>();
        long ticks = Math.max(1L, Args.integer(step.args(), "ticks", 20));
        context.scheduler().runLater(ticks, () -> future.complete(ExecutionSignal.CONTINUE));
        return future;
    }

    private static CompletableFuture<ExecutionSignal> runCommand(ActionStep step, ScriptExecutionContext context) {
        String command = Args.str(step.args(), "command", "say Scratchy command");
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, command);
        return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
    }

    private static CompletableFuture<ExecutionSignal> setVariable(ActionStep step, ScriptExecutionContext context) {
        String key = Args.str(step.args(), "key", "default");
        Object value = step.args().getOrDefault("value", "");
        context.variables().put(key, value);
        return CompletableFuture.completedFuture(ExecutionSignal.CONTINUE);
    }

    private static boolean hasItem(ConditionSpec condition, ScriptExecutionContext context) {
        return context.player().map(player -> {
            Material material = Material.matchMaterial(Args.str(condition.args(), "material", "STONE"));
            int minimum = Math.max(1, Args.integer(condition.args(), "amount", 1));
            if (material == null) {
                return false;
            }
            return player.getInventory().contains(material, minimum);
        }).orElse(false);
    }

    private static boolean nearbyEntity(ConditionSpec condition, ScriptExecutionContext context) {
        return context.player().map(player -> {
            EntityType type = EntityType.fromName(Args.str(condition.args(), "entity", "ZOMBIE"));
            double radius = Math.max(1.0, Args.decimal(condition.args(), "radius", 5.0));
            return player.getNearbyEntities(radius, radius, radius).stream()
                    .anyMatch(entity -> type == null || entity.getType() == type);
        }).orElse(false);
    }

    private static boolean randomChance(ConditionSpec condition, ScriptExecutionContext context) {
        double chance = Math.max(0.0, Math.min(1.0, Args.decimal(condition.args(), "chance", 0.5)));
        return Math.random() <= chance;
    }

    private static boolean blockMatch(ConditionSpec condition, ScriptExecutionContext context) {
        return context.player().map(player -> {
            World world = Bukkit.getWorld(Args.str(condition.args(), "world", player.getWorld().getName()));
            Material material = Material.matchMaterial(Args.str(condition.args(), "material", "STONE"));
            if (world == null || material == null) {
                return false;
            }
            int x = Args.integer(condition.args(), "x", player.getLocation().getBlockX());
            int y = Args.integer(condition.args(), "y", player.getLocation().getBlockY());
            int z = Args.integer(condition.args(), "z", player.getLocation().getBlockZ());
            Block block = world.getBlockAt(x, y, z);
            return block.getType() == material;
        }).orElse(false);
    }

    private static boolean healthThreshold(ConditionSpec condition, ScriptExecutionContext context) {
        return context.player().map(player -> {
            double threshold = Args.decimal(condition.args(), "value", 10.0);
            String op = Args.str(condition.args(), "operator", "<=");
            double health = player.getHealth();
            return switch (op) {
                case "<" -> health < threshold;
                case ">" -> health > threshold;
                case ">=" -> health >= threshold;
                case "==" -> health == threshold;
                default -> health <= threshold;
            };
        }).orElse(false);
    }

    private static CompletableFuture<ExecutionSignal> withPlayer(ScriptExecutionContext context, PlayerAction action) {
        return context.player().map(action::execute)
                .orElseGet(() -> CompletableFuture.completedFuture(ExecutionSignal.CONTINUE));
    }

    @FunctionalInterface
    private interface PlayerAction {
        CompletableFuture<ExecutionSignal> execute(Player player);
    }
}
