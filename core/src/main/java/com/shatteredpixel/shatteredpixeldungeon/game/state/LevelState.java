package com.shatteredpixel.shatteredpixeldungeon.game.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure level state.
 *
 * This is not a renderer and not a generator.
 * It should only describe the level state that server can simulate
 * and clients can display.
 */
public class LevelState {

    public long levelId;

    public int depth;
    public int branch;

    public int width;
    public int height;

    public int[] terrain;

    /**
     * First milestone can use shared visibility.
     * Later this should become per-player visibility.
     */
    public boolean[] visited;
    public boolean[] mapped;

    public final List<MobState> mobs = new ArrayList<>();
    public final List<HeapState> heaps = new ArrayList<>();
    public final List<TrapState> traps = new ArrayList<>();

    public LevelState() {
    }

    public boolean containsCell(int cell) {
        return terrain != null && cell >= 0 && cell < terrain.length;
    }
}
