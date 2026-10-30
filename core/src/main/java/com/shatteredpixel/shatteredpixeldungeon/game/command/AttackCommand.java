package com.shatteredpixel.shatteredpixeldungeon.game.command;

/**
 * Request to attack target cell or target entity.
 */
public class AttackCommand extends PlayerCommand {

    public int targetCell;
    public long targetEntityId;

    public AttackCommand() {
    }

    public AttackCommand(long playerId, long actorEntityId, int targetCell, long targetEntityId) {
        super(playerId, actorEntityId);
        this.targetCell = targetCell;
        this.targetEntityId = targetEntityId;
    }

    @Override
    public String type() {
        return "ATTACK";
    }
}