package com.shatteredpixel.shatteredpixeldungeon.network.dto.state;

public class HeroStateDto {

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

    public boolean alive;
    public boolean ready;

    public float actionTime;

    public long inventoryId;

    public BuffStateDto[] buffs;

    public HeroStateDto() {
    }
}