package com.shatteredpixel.shatteredpixeldungeon.network.dto.command;

public class PlayerCommandDto {

    public long commandId;
    public long playerId;
    public long actorEntityId;
    public int clientSequence;
    public long baseServerSequence;

    public String type; // "MOVE", "ATTACK", "INTERACT", "PICKUP", "OPEN_CHEST", "BUY", "UNLOCK", "MINE", "TRANSITION"

    public int targetCell;
    public int targetEntityId; // Оставляем int, так как ID актеров в движке игры - это int

    public PlayerCommandDto() {}
}