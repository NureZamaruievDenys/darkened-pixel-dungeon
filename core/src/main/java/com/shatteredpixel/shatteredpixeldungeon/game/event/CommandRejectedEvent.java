package com.shatteredpixel.shatteredpixeldungeon.game.event;

/**
 * Event emitted when server rejects a player command.
 */
public class CommandRejectedEvent extends GameEvent {

    public long commandId;
    public long playerId;

    public String reason;

    public CommandRejectedEvent() {
    }

    public CommandRejectedEvent(long commandId, long playerId, String reason) {
        this.commandId = commandId;
        this.playerId = playerId;
        this.reason = reason;
    }

    @Override
    public String type() {
        return "COMMAND_REJECTED";
    }
}
