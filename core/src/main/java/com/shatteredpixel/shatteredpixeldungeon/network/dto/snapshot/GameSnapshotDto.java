package com.shatteredpixel.shatteredpixeldungeon.network.dto.snapshot;

import com.shatteredpixel.shatteredpixeldungeon.network.dto.state.GameStateDto;

public class GameSnapshotDto {

    public long snapshotId;
    public long sequence;
    public long turn;

    public GameStateDto state;

    public GameSnapshotDto() {
    }
}
