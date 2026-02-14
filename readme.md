# XP Hunter

XP Hunter is a 2D action RPG built in Java (Swing/AWT) with tile-based maps, combat, exploration, inventory management, NPC interactions, trading, puzzles, boss encounters, and save/load persistence.

## Current Functionality

### Core Gameplay
- Real-time top-down movement (WASD), including diagonal movement.
- Directional melee combat with weapon-dependent attack animations.
- Guarding system (`SPACE`) with parry/guard behavior.
- Ranged attack / spell projectile system (`F`) with resource checks.
- Dash ability (`SHIFT`) with cooldown, movement collision checks, and visual afterimage trail.
- Contact damage, knockback, invincibility frames, and transparency feedback when hit.

### Player Progression
- Player stats: `level`, `life`, `mana`, `strength`, `dexterity`, `attack`, `defense`, `exp`, `coin`.
- XP gain from defeated enemies.
- Level-up system with scaling required XP (`nextLevelExp`) and stat growth.
- Equipment system for weapon and shield.

### Items and Inventory
- Inventory with stackable and non-stackable items.
- Item categories: weapon, shield, consumable, light source, pickup-only, obstacle, etc.
- Usable consumables and keys.
- Lootable chests with opened/closed state.
- Door unlocking with keys.
- Pickups and drops (coins, hearts, mana crystals, potions, tools, etc.).

### NPCs and Interaction
- Dialogue system with typewriter-style text reveal.
- NPC interaction on `ENTER`.
- Merchant NPC with buy/sell trade UI and inventory.
- Pushable Big Rock puzzle NPC behavior.

### World and Exploration
- Multi-map world structure (`maxMap = 10`, multiple populated maps).
- Tile collision, object collision, entity collision.
- Event trigger system (teleports, hazards, boss trigger zones).
- Area transitions (outside/indoor/dungeon) with music switching.
- Interactive tiles:
  - Dry trees
  - Destructible walls
  - Metal plates

### Boss and Cutscene Flow
- Skeleton Lord boss encounter with cutscene sequencing.
- Temporary arena door lock-in during boss intro.
- Boss phases/rage behavior and custom boss HP bar.
- Boss defeat progression flag (`Progress.skeletonLordDefeated`) and cleanup logic.

### UI / HUD
- Hearts-based life HUD.
- Mana crystal HUD.
- Dash cooldown HUD bar.
- XP bar at bottom with current level and XP/next level.
- Floating message log.
- Monster HP bars (normal + boss format).
- Character screen with full stats and equipped gear.
- Inventory UI for player and NPC trade contexts.
- Minimap and full map screen.

### Game States and Menus
- Title screen (`New Game`, `Load Game`, `Quit`).
- Play, Pause, Dialogue, Character, Options, Trade, Transition, Sleep, Map, Cutscene, and Game Over states.
- Options menu:
  - Fullscreen toggle
  - Music/SFX volume
  - Controls page
  - End game confirmation

### Save / Load
- Binary serialization save system (`save.dat`) for:
  - Player stats
  - Inventory and item amounts
  - Equipped weapon/shield slots
  - Map object state (position, opened state, loot assignment)
- Defensive handling for unknown/unsupported saved objects during load.

### Audio and Visual Systems
- Background music with area/boss transitions.
- Sound effects for combat, interaction, UI, events.
- Lighting/environment manager with sleep transition fade.
- Particle effects for impacts/destruction.
- Render sorting by world Y for depth illusion.

### Debug / Developer Tools
- Toggle debug overlay (`T`) with world position and diagnostics.
- God mode toggle (`G`).

## Controls

### In-game
- `W A S D`: Move
- `ENTER`: Confirm / attack / interact
- `F`: Shoot / cast projectile
- `SPACE`: Guard
- `SHIFT`: Dash
- `C`: Character screen
- `P`: Pause
- `M`: Full map
- `TAB`: Toggle minimap
- `ESC`: Options

### Debug
- `T`: Toggle debug text
- `G`: Toggle god mode

## Project Structure (high-level)
- `src/main`: game loop, input, rendering orchestration, UI, events, collision, setup
- `src/entity`: player, NPCs, shared entity logic, particles
- `src/monster`: enemy and boss implementations
- `src/objects`: items, doors, chests, utility interactables
- `src/tile` and `src/tile_interactive`: map tiles and interactive world tiles
- `src/data`: save/load and progression data

## Notes
- This project is a continued/customized development inspired by RyiSnow's Java RPG tutorial series.
