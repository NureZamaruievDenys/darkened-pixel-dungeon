package com.shatteredpixel.shatteredpixeldungeon.network.dto.command;

public class CommandBatchDto {

    public long playerId;

    public int firstClientSequence;
    public int lastClientSequence;

    public PlayerCommandDto[] commands;

    public CommandBatchDto() {
    }
}