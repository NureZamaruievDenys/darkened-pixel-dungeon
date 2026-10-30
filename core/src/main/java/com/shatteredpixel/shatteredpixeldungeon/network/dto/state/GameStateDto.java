package com.shatteredpixel.shatteredpixeldungeon.network.dto.state;

public class GameStateDto {

    public long gameId;

    public long seed;

    public int depth;
    public int branch;

    public long globalTurn;
    public long serverSequence;

    public PlayerStateDto[] players;
    public HeroStateDto[] heroes;
    public LevelStateDto[] levels;

    public GameStateDto() {
    }
}