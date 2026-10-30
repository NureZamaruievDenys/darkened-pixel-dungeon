package com.shatteredpixel.shatteredpixeldungeon.network.dto.state;

public class BuffStateDto {

    public long buffId;

    public String buffType;

    public long targetEntityId;
    public long sourceEntityId;

    public float duration;
    public int stacks;

    public BuffStateDto() {
    }
}