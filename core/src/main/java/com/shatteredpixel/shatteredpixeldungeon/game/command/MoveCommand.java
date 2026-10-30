package com.shatteredpixel.shatteredpixeldungeon.game.command;

/**
 * Request to move controlled actor to target cell.
 */
public class MoveCommand extends PlayerCommand {

    public int targetCell;

    public MoveCommand() {
    }

    public MoveCommand(long playerId, long actorEntityId, int targetCell) {
        super(playerId, actorEntityId);
        this.targetCell = targetCell;
    }

    @Override
    public String type() {
        return "MOVE";
    }
}