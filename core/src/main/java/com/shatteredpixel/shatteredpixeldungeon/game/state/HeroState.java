package com.shatteredpixel.shatteredpixeldungeon.game.state;

import java.util.ArrayList;
import java.util.List;

public class HeroState {
    public long entityId;
    public long ownerPlayerId;

    public String heroClass;
    public String heroSubClass;

    public int depth;
    public int branch;
    public int pos;

    public int hp;
    public int maxHp;

    public int level;
    public int exp;
    public int strength;

    public boolean alive = true;
    public boolean ready = false;

    public float actionTime;

    public long inventoryId = GameIds.NONE;

    public final List<BuffState> buffs = new ArrayList<>();

    public HeroState() {
    }
}

