package com.shatteredpixel.shatteredpixeldungeon.game.save;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.game.state.MobState;

/**
 * Converts legacy Mob object to pure MobState.
 */
public class LegacyMobStateExporter {

    private final LegacyIdRegistry ids;

    public LegacyMobStateExporter(LegacyIdRegistry ids) {
        this.ids = ids;
    }

    public MobState export(Mob mob, int depth, int branch) {
        if (mob == null) {
            return null;
        }

        MobState state = new MobState();

        state.entityId = ids.idFor(mob);

        state.mobType = mob.getClass().getName();

        state.depth = depth;
        state.branch = branch;
        state.pos = mob.pos;

        state.hp = mob.HP;
        state.maxHp = mob.HT;

        state.alive = mob.isAlive();

        return state;
    }
}