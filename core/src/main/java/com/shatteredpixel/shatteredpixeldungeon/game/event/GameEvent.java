package com.shatteredpixel.shatteredpixeldungeon.game.event;

/**
 * Base class for simulation events.
 *
 * Events describe what happened.
 * They should not execute visuals or sounds directly.
 */
public abstract class GameEvent {

    public long eventId;
    public long serverSequence;
    public long turn;

    public abstract String type();
}
