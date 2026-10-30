package com.shatteredpixel.shatteredpixeldungeon.game.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure mob state.
 */
public class MobState {

    public long entityId;

    public String mobType;

    public int depth;
    public int branch;
    public int pos;

    public int hp;
    public int maxHp;

    public boolean alive = true;

    public float actionTime;

    public long targetEntityId = GameIds.NONE;
    public int lastKnownTargetPos = -1;

    public final List<BuffState> buffs = new ArrayList<>();

    public MobState() {
    }
}

