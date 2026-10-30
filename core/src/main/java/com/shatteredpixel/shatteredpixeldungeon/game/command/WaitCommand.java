package com.shatteredpixel.shatteredpixeldungeon.game.command;

/**
 * Explicit wait/skip-turn command.
 */
public class WaitCommand extends PlayerCommand {

    public WaitCommand() {
    }

    public WaitCommand(long playerId, long actorEntityId) {
        super(playerId, actorEntityId);
    }

    @Override
    public String type() {
        return "WAIT";
    }
}