package com.shatteredpixel.shatteredpixeldungeon.network.dto.session;

import com.shatteredpixel.shatteredpixeldungeon.network.dto.snapshot.GameSnapshotDto;

public class JoinResponseDto {

    public boolean accepted;

    public String reason;

    public long assignedPlayerId;

    public boolean host;

    public GameSnapshotDto initialSnapshot;

    public JoinResponseDto() {
    }
}