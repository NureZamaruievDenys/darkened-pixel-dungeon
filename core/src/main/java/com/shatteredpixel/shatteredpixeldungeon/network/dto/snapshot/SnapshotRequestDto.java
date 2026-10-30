package com.shatteredpixel.shatteredpixeldungeon.network.dto.snapshot;

public class SnapshotRequestDto {

    public long playerId;
    public long lastKnownSequence;

    public String reason;

    public SnapshotRequestDto() {
    }
}