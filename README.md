# ScratchyPlugin (Paper)

ScratchyPlugin is a Minecraft Paper plugin that provides a **Scratch-inspired visual scripting editor** entirely in-game using inventory GUIs.

Players can create scripts with triggers and block-like actions/logic, save them to disk, and execute them from in-game events or commands.

## Supported Paper Version
- Paper `1.21.11` API (Java 21)

## Build & Install
```bash
./gradlew clean build
```

Install the generated shaded JAR from `build/libs/` into your server `plugins/` folder.

## Commands
- `/scratchplugin` (or `/scratchplugin open`) – open main GUI
- `/scratchplugin list` – list projects + scripts
- `/scratchplugin run <scriptName>` – run script manually
- `/scratchplugin reload` – reload persisted project data
- `/scratchplugin debug` – toggle runtime debug logging

Legacy alias command: `/scratchy`.

## Permissions
- `scratchplugin.use`
- `scratchplugin.edit`
- `scratchplugin.run`
- `scratchplugin.admin`
- `scratchplugin.debug`

## In-Game Editing Workflow
1. Run `/scratchplugin open`.
2. Create/open a project from the main menu.
3. Open the script editor and assign trigger.
4. Add blocks from the block palette (actions/control flow examples).
5. Save script from editor.
6. Trigger via command/event or run manually with `/scratchplugin run <scriptName>`.

## Supported Trigger Types
- Command
- Player join
- Player interact
- Block break
- Entity death
- Scheduled repeating

## Supported Action Blocks
- give item
- spawn mob
- send message
- play sound
- teleport
- place block
- remove block
- apply effect
- wait/delay
- run command
- set variable
- increment variable
- stop script

## Supported Conditions / Control Flow
- has item
- nearby entity
- random chance
- block material match
- health threshold
- variable equals
- variable numeric comparison
- sequence
- if / else
- repeat N
- repeat while
- stop

## Persistence Format
Projects are persisted as YAML files in:
- `plugins/Scratchy/projects/*.yml`

Persistence is isolated behind `ProjectRepository` and `ProjectService`, with domain model serialization handled by Jackson YAML.

## Architecture Overview
- **Domain (`domain`)**: immutable script graph nodes (`SequenceStep`, `ActionStep`, `IfStep`, loops, etc.).
- **Engine (`engine`)**: step execution, async wait scheduling, and trigger runtime.
- **Registries (`registry`)**: action/condition handler registration for extension without giant switch blocks.
- **Persistence (`persistence`)**: repository abstraction and project catalog service.
- **GUI (`gui` + listeners)**: inventory-driven editor and palette with player session state.
- **Validation (`validation`)**: script integrity checks before execution.

## Debugging
Use `/scratchplugin debug` to enable execution logging.
Invalid scripts are skipped with explicit validation errors in server logs.

## Example Script
See `examples/welcome-script.yml`.

## How to Add a New Block Type
1. Add a new action/condition key and handler in `BuiltinRegistries` (or your own registrar).
2. Add GUI item wiring in `ScriptPaletteGui` + `GuiListener`.
3. Add validation rules in `ScriptValidator` if needed.
4. Add tests in `src/test/java`.

## Limitations
- GUI parameter editing is currently lightweight and not yet full anvil/sign editors for every argument.
- Nested visual tree editing is represented as templates/examples in editor interactions.
- No collaborative multi-user editing lock/merge strategy yet.

## Future Improvements
- drag-and-drop block arrangement
- richer nested visual blocks and branch editing
- multiplayer collaborative editing sessions
- script templates and marketplace-like import/export
- richer variable types + scoped variable lifetimes
- event parameter references (e.g., interacted block/entity as first-class values)
- stronger persistence migration/versioning
