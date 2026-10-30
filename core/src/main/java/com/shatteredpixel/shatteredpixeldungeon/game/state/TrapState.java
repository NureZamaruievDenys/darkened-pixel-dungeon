package com.shatteredpixel.shatteredpixeldungeon.game.state;

/**
 * Pure trap state.
 */
public class TrapState {

    public long trapId;

    public String trapType;

    public int pos;

    public boolean active;
    public boolean visible;

    public TrapState() {
    }
}