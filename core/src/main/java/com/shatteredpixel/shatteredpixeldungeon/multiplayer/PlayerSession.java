package com.shatteredpixel.shatteredpixeldungeon.multiplayer;

/**
 * Server-side connected player session.
 *
 * This class is transport-neutral:
 * it does not know about KryoNet Connection yet.
 */
public class PlayerSession {

    public long playerId;

    public String playerName;

    public boolean host;

    public boolean connected = true;

    public long controlledHeroId;

    public int lastClientSequence;
    public long lastAcknowledgedServerSequence;

    public PlayerSession() {
    }

    public PlayerSession(long playerId, String playerName, boolean host) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.host = host;
    }
}