package com.shatteredpixel.shatteredpixeldungeon.game.state;

public class BuffState {
    
    public long buffId;

    public String buffType;

    public long targetEntityId;
    public long sourceEntityId = GameIds.NONE;

    public float duration;
    public int stacks;

    public BuffState() {
    }
}
