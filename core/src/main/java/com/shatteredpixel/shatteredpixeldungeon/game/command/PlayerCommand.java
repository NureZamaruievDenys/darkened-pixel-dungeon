package com.shatteredpixel.shatteredpixeldungeon.game.command;

/**
 * Base class for player-issued commands.
 *
 * In multiplayer:
 * client creates command -> sends to server -> server validates -> server applies.
 */
public abstract class PlayerCommand {

    public long commandId;

    public long playerId;
    public long actorEntityId;

    public int clientSequence;

    /**
     * Last server sequence known by client when command was created.
     * Useful later for reconciliation/desync diagnostics.
     */
    public long baseServerSequence;

    protected PlayerCommand() {
    }

    protected PlayerCommand(long playerId, long actorEntityId) {
        this.playerId = playerId;
        this.actorEntityId = actorEntityId;
    }

    public abstract String type();
}