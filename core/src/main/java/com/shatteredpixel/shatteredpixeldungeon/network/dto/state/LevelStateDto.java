package com.shatteredpixel.shatteredpixeldungeon.network.dto.state;

public class LevelStateDto {

    public long levelId;

    public int depth;
    public int branch;

    public int width;
    public int height;

    public int[] terrain;

    public boolean[] visited;
    public boolean[] mapped;

    public MobStateDto[] mobs;
    public HeapStateDto[] heaps;
    public TrapStateDto[] traps;

    public LevelStateDto() {
    }
}