package com.shatteredpixel.shatteredpixeldungeon.network.dto.delta;

import com.shatteredpixel.shatteredpixeldungeon.network.dto.event.GameEventDto;

public class StateDeltaDto {

    public long baseSequence;
    public long sequence;
    public long turn;

    public EntityDeltaDto[] entityDeltas;
    public GameEventDto[] events;

    public StateDeltaDto() {
    }
}