# Scratchy (Paper Plugin)

Scratchy is a Paper plugin that provides an in-game, inventory-GUI-based visual scripting editor inspired by Scratch.

## Features
- Inventory GUI editor for scripts/projects
- Triggers: command, join, interact, block break, entity death, scheduled
- Actions: give item, spawn mob, send message, play sound, teleport, place/remove block, apply effect, wait, run command, set variable
- Conditions: has item, nearby entity, random chance, block match, health threshold
- Control flow: sequence, if/else, repeat N, repeat while (with safety guard), stop
- YAML persistence per project
- Modular architecture (GUI, engine, persistence, listeners, domain model, registries)
- Debug mode and safe error handling

## Commands
- `/scratchy open` - open project GUI editor
- `/scratchy list` - list loaded projects
- `/scratchy debug` - toggle debug mode
- `/scratchy trigger <key>` - run command-trigger scripts by key

## Permissions
- `scratchy.use` (default: true)
- `scratchy.admin` (default: op)

## Persistence format
Projects are stored under `plugins/Scratchy/projects/*.yml`.

## Development
```bash
./gradlew build
./gradlew test
```

## Example scripts
See `examples/welcome-script.yml`.

## Architecture notes
- **Domain model** is immutable records / sealed step interfaces.
- **Execution engine** executes step trees asynchronously with completable futures.
- **Extensibility** comes from `ActionRegistry` and `ConditionRegistry` to avoid giant switch blocks.
- **Persistence** is isolated in repository/service layer.
- **GUI/listeners** are thin orchestration layers around the core engine.
