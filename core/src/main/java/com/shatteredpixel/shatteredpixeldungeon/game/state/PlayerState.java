package com.shatteredpixel.shatteredpixeldungeon.game.state;

public class PlayerState {

    /**
     * Network/session player state.
     *
     * This is not the same as HeroState:
     * PlayerState describes the connected human player,
     * HeroState describes the in-game character controlled by that player.
     */

    public enum Status {
        LOBBY,
        LOADING,
        PLAYING,
        DISCONNECTED,
        DEAD,
        SPECTATOR
    }

    public long playerId;
    public String name;

    public boolean host;

    public long controlledHeroId = GameIds.NONE;

    public int currentDepth;
    public int currentBranch;

    public Status status = Status.LOBBY;

    public int lastClientSequence;
    public long lastAcknowledgedServerSequence;

    public PlayerState() {
    }
}

