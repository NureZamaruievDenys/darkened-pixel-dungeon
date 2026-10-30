package com.shatteredpixel.shatteredpixeldungeon.network.dto.state;

public class MobStateDto {

    public long entityId;

    public String mobType;

    public int depth;
    public int branch;
    public int pos;

    public int hp;
    public int maxHp;

    public boolean alive;

    public float actionTime;

    public long targetEntityId;
    public int lastKnownTargetPos;

    public BuffStateDto[] buffs;

    public MobStateDto() {
    }
}