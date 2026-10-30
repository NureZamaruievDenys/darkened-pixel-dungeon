package com.shatteredpixel.shatteredpixeldungeon.game.delta;

import com.shatteredpixel.shatteredpixeldungeon.game.event.GameEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain-level state delta.
 *
 * Later this can be mapped to network DTO and sent via KryoNet.
 */
public class StateDelta {

    public long baseSequence;
    public long sequence;
    public long turn;

    public final List<EntityDelta> entityDeltas = new ArrayList<>();
    public final List<GameEvent> events = new ArrayList<>();

    public StateDelta() {
    }

    public boolean isEmpty() {
        return entityDeltas.isEmpty() && events.isEmpty();
    }
}