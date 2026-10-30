package com.shatteredpixel.shatteredpixeldungeon.game.save;

import com.shatteredpixel.shatteredpixeldungeon.game.state.TrapState;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;

/**
 * Converts legacy Trap object to pure TrapState.
 */
public class LegacyTrapStateExporter {

    private final LegacyIdRegistry ids;

    public LegacyTrapStateExporter(LegacyIdRegistry ids) {
        this.ids = ids;
    }

    public TrapState export(Trap trap, int pos) {
        if (trap == null) {
            return null;
        }

        TrapState state = new TrapState();

        state.trapId = ids.idFor(trap);

        state.trapType = trap.getClass().getName();

        state.pos = pos;

        state.active = trap.active;
        state.visible = trap.visible;

        return state;
    }
}
