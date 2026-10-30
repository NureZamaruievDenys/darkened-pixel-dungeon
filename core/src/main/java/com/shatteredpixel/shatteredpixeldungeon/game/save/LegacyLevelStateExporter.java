package com.shatteredpixel.shatteredpixeldungeon.game.save;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.game.state.HeapState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.LevelState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.MobState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.TrapState;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

/**
 * Converts legacy Level object to pure LevelState.
 */
public class LegacyLevelStateExporter {

    private final LegacyIdRegistry ids;

    private final LegacyMobStateExporter mobExporter;
    private final LegacyHeapStateExporter heapExporter;
    private final LegacyTrapStateExporter trapExporter;

    public LegacyLevelStateExporter(LegacyIdRegistry ids) {
        this.ids = ids;
        this.mobExporter = new LegacyMobStateExporter(ids);
        this.heapExporter = new LegacyHeapStateExporter(ids);
        this.trapExporter = new LegacyTrapStateExporter(ids);
    }

    public LevelState export(Level level) {
        if (level == null) {
            return null;
        }

        LevelState state = new LevelState();

        state.levelId = ids.idFor(level);

        state.depth = Dungeon.depth;
        state.branch = Dungeon.branch;

        state.width = level.width();
        state.height = level.height();

        state.terrain = level.map == null ? null : level.map.clone();
        state.visited = level.visited == null ? null : level.visited.clone();
        state.mapped = level.mapped == null ? null : level.mapped.clone();

        if (level.mobs != null) {
            for (Mob mob : level.mobs) {
                MobState mobState = mobExporter.export(mob, state.depth, state.branch);
                if (mobState != null) {
                    state.mobs.add(mobState);
                }
            }
        }

        if (level.heaps != null) {
            for (Heap heap : level.heaps.valueList()) {
                HeapState heapState = heapExporter.export(heap);
                if (heapState != null) {
                    state.heaps.add(heapState);
                }
            }
        }

        if (level.traps != null) {
            for (int pos : level.traps.keyArray()) {
                TrapState trapState = trapExporter.export(level.traps.get(pos), pos);
                if (trapState != null) {
                    state.traps.add(trapState);
                }
            }
        }

        return state;
    }
}
