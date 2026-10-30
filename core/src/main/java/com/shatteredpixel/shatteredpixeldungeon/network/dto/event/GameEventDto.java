package com.shatteredpixel.shatteredpixeldungeon.network.dto.event;

/**
 * Generic event DTO for early multiplayer prototype.
 *
 * Later this can be replaced with typed event DTO classes if needed.
 */
public class GameEventDto {

    public long eventId;
    public long serverSequence;
    public long turn;

    public String type;

    public long entityId;
    public long targetEntityId;
    public long commandId;
    public long playerId;

    public int fromCell;
    public int toCell;
    public int targetCell;

    public int amount;

    public String reason;
    public String message;

    public GameEventDto() {
    }
}