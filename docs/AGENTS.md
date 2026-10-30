# AI Coding Agents Guide - Shattered Pixel Dungeon

## Project Overview
**Shattered Pixel Dungeon** is a turn-based roguelike dungeon crawler written in Java using LibGDX framework. It compiles for Android, Desktop (via Desktop module), and iOS. The codebase emphasizes **Singleton pattern** for game state management, **Actor-based turn scheduling**, and **modular subsystems** (Actors, Items, Levels, Scenes).

---

## Architecture Essentials

### Game State Singleton
**File**: `../core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Dungeon.java` (33 KB)

The **Dungeon class is static/singleton** and holds ALL game state:
```java
public static Hero hero;                    // Main character
public static Level level;                  // Current level
public static int depth;                    // Depth 1-26
public static ArrayList<Integer> generatedLevels;  // Cached levels
public static long seed;                    // RNG seed for deterministic generation
```

**Critical**: Any persistent game data (hero, level, resources) must flow through `Dungeon` methods:
- `Dungeon.init()` - Start new game
- `Dungeon.newLevel()` - Generate/load level
- `Dungeon.saveAll()` / `Dungeon.loadAll()` - Bundle serialization
- **Every modification to game state must update Dungeon, not create local copies**

### Turn-Based Actor System
**Files**: `core/actors/Actor.java`, `core/actors/Char.java`

All game entities (Hero, Mobs, visual effects, buffs) inherit from **Actor** with priority-based scheduling:
- Each Actor has `time` (internal clock) and `actPriority` (execution order)
- Priority values: `VFX_PRIO (100)` > `HERO_PRIO (0)` > `BLOB_PRIO (-10)` > `MOB_PRIO (-20)` > `BUFF_PRIO (-30)`
- Main loop: `while (Actor.hasPending()) { Actor.next().act(); }` executes actors in time order
- **spend(time)** method advances actor's clock; can be modified by Buffs (Haste/Slow)

**Example workflow for hero click**:
1. Hero click → `GameScene.selectCell(pos)`
2. `hero.attack(pos)` → `hero.spend(1.2f)` (action cost)
3. Actor queue processes: Mobs act, then Blobs, then Buffs
4. Each entity returns true from `act()` if it acted, false if waiting for input

### Level Architecture
**File**: `core/levels/Level.java` and subclasses

- **Map**: int array of Terrain types (0-31 constants in Terrain.java)
- **Generation**: `Builder.build()` → `Painter.paint()` → `createMobs()/createItems()`
- **Level types by depth**: SewerLevel (1-2), PrisonLevel (3-4), CavesLevel (5-6), etc.
- **Generation randomness**: Uses static `Random` seeded via `Dungeon.seed` (deterministic replays)
- **Persistence**: Levels cached in `Dungeon.generatedLevels` to avoid regeneration

### Item Hierarchy
**Files**: `core/items/Item.java` and subpackages (armor, weapons, artifacts, potions, etc.)

Items are managed through:
- **Heap** class: One heap per floor position
- **Generator** class: Loot generation with `LimitedDrops` enum for rare item caps
- **Ownership**: In inventory (Hero.Belongings), on floor (Level.heaps), or equipment slots
- **Enhancement**: Uses `level` field for upgrade tracking (weapons/armor 0-15 levels)
- **Curses**: Tracked via `cursed` flag; affects identification and behavior

### Scene/UI System
**File**: `core/scenes/GameScene.java`, base: `core/scenes/PixelScene.java`

- **Scenes**: Full-screen views (TitleScene, GameScene, JournalScene, etc.)
- **Windows**: Modal dialogs (WndInventory, WndOptions) - in `core/windows/`
- **Components**: Reusable UI elements (Toolbar, HealthBar, BuffIndicator) - in `core/ui/`
- **GameScene**: Handles level rendering, player input routing, and win/lose conditions
- **PixelScene**: Base class with camera, scene transitions via `Game.switchScene()`

---

## Critical Developer Workflows

### Building & Running

**Desktop Debug** (fastest for iteration):
```bash
./gradlew desktop:debug
```
Launches immediate playable game; hot-reload not available—must rebuild.

**Desktop Release**:
```bash
./gradlew desktop:release
```
Produces optimized JAR at `/desktop/build/libs/`.

**Android** (requires Android SDK):
```bash
./gradlew android:assembleDebug  # APK at android/build/outputs/apk/debug/
```

**Project Structure**:
- `../core` - Shared game logic (platform-independent)
- `../desktop`, `android/`, `ios/` - Platform-specific entry points
- `../SPD-classes` and `services/` - Shared dependencies

### Gradle Configuration
**File**: `../build.gradle` (root) and `core/build.gradle`

- Java compatibility: **VERSION_11** (minimum Java 11 required)
- LibGDX version: **1.14.0**
- Gradle: **9.1.0**; JVM args up to **2GB** heap (gradle.properties)
- All version constants in build.gradle root `ext {...}` block

### Debugging & Common Issues

**Cant compile**: Check Java version (`java -version` should be 11+)
**Memory errors**: Gradle.properties already allocates 2GB; increase if needed
**Asset not found**: All game assets (sprites, sounds, music) referenced in `Assets.java`; check paths match actual files
**Physics issues**: Check actor time/priority or level terrain values

---

## Project-Specific Conventions

### Serialization Pattern (Bundle/Bundlable)
**Files**: `core/utils/Bundle.java` and interfaces/implementations everywhere

All persistent objects implement **Bundlable**:
```java
public void storeInBundle(Bundle bundle) { /* save to HashMap */ }
public void restoreFromBundle(Bundle bundle) { /* load from HashMap */ }
```
This enables full game serialization. When adding new Dungeon properties, update both `init()` and serialization methods.

### String Localization
**File**: `core/messages/Messages.java`

All UI text must use `Messages.get("KEY")` not hardcoded strings. Keys defined in `*.properties` files. Add new strings as keys, not inline text.

### Random Number Generation
**Pattern**: Use injected `Random` (seeded by `Dungeon.seed`), not `Math.random()`.

Deterministic replays require consistent RNG—don't use `java.util.Random` directly; field must follow rules for seeding.

### Buff/Debuff Application
**File**: `core/actors/buffs/Buff.java`

Buffs attach to Char via `buff.attachTo(char)` not direct list manipulation. Implement:
- `act()` - Execute this buff's turn logic
- `detach()` - Remove from character
- `prolonged` - Whether buff persists across `prevent()` calls

---

## Cross-Component Integration Points

### When Adding a New Enemy Type
1. Create class extending **Mob** in `actors/mobs/`
2. Override `act()` for AI logic (view cone, pathfinding, attack)
3. Add sprite in `sprites/` directory
4. Register drops in `items/Generator.java`
5. Ensure added to level spawn pool in `levels/Level.createMobs()`

### When Adding a New Item
1. Create class extending **Item** or **EquipableItem** in `items/` subpackage
2. Implement `doAction()` and `actions()` for interaction menu
3. Create `ItemSprite` if visual representation needed
4. Add to `Generator.randomItem()` with weight if loot-droppable
5. Add localization key to Messages properties files

### When Adding a New Level Feature
1. Modify `Level` generation in `levels/Room.java` or `builders/`
2. Add corresponding `Terrain` type if needed
3. Update `DungeonTilemap.java` for visual rendering
4. Test `Dungeon.seed` repeatability by running with same seed twice

### Event/Action Flow
- **Input**: SPDAction maps keys/touches to Actions
- **Handler**: GameScene routes to hero (movement/attack) or UI (inventory/menu)
- **State Change**: Dungeon.hero gets updated, triggers Actor queue
- **Persistence**: GameScene detects level-end, calls Dungeon.saveAll()

---

## Key Files Reference

| Component | Key File | Purpose |
|-----------|----------|---------|
| **Core Game** | `Dungeon.java` | Game state singleton—source of truth |
| **Actor System** | `Actor.java`, `Char.java` | Base classes for all acting entities |
| **Hero/Player** | `Hero.java` | Main character with abilities/inventory |
| **Enemies** | `Mob.java` + subclasses | All non-player entities |
| **Items** | `Item.java`, `Generator.java` | Item models and loot generation |
| **Levels** | `Level.java`, `RegularLevel.java`, `Room.java` | Map generation and structure |
| **Main Game Loop** | `GameScene.java` | Input processing, actor scheduling, rendering |
| **Entry Point** | `ShatteredPixelDungeon.java`, `PixelScene.java` | Application bootstrap |
| **Settings/Persistence** | `SPDSettings.java`, `GamesInProgress.java` | Player preferences and save management |
| **UI Components** | `scenes/`, `ui/`, `windows/` | All visual interface elements |

---

## Common Pitfalls & Solutions

| Issue | Solution |
|-------|----------|
| Game state becomes inconsistent after saving | Ensure ALL changed fields call `Dungeon.saveAll()` and implement Bundlable correctly |
| Level not showing changes | Regeneration cached; verify depth not in `generatedLevels` or clear cache |
| Actor actions out of order | Check `actPriority` value and `spend()` calls—priority lower number = earlier execution |
| New item/mob not appearing | Verify added to Generator weights and level spawn pools; check sprite atlas exists |
| Performance drops in late game | Review Actor queue size via logs; ensure dead entities removed via `Actor.remove()` |
| Changes not applied to running game | Rebuild with `./gradlew desktop:debug`; no hot-reload available |

---

## Relevant Documentation
- `/docs/ARCHITECTURE.md` - Comprehensive system design (Russian; covers core patterns)
- `/docs/MAIN_FILES_STRUCTURE.md` - Detailed class-by-class breakdown
- `/docs/PRACTICAL_GUIDE.md` - Code examples and workflow patterns
- `/docs/getting-started-desktop.md` - Build & setup instructions

---

## Quick Checklists

**Before modifying Dungeon state**:
- [ ] Is this data persistent? If yes, update `storeInBundle()` and `restoreFromBundle()`
- [ ] Does this affect RNG determinism? If yes, ensure seeded correctly
- [ ] Should old saves be migrated? Check version logic in load methods

**Before committing code changes**:
- [ ] Built successfully with `./gradlew desktop:debug`?
- [ ] Game runs without errors on desktop?
- [ ] All new strings in Messages.get() with localization keys?
- [ ] Serialization handled if new persistent fields added?

---

**Last Updated**: June 2026  
**Game Version**: 3.3.8 | **Java**: 11+ | **LibGDX**: 1.14.0

