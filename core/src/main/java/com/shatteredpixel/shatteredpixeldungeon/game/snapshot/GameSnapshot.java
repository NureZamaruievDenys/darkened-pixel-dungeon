package com.shatteredpixel.shatteredpixeldungeon.game.snapshot;

import com.shatteredpixel.shatteredpixeldungeon.game.state.GameState;

/**
 * Full or partial game snapshot.
 *
 * First prototype can use full snapshots.
 * Later we can add visibility-filtered snapshots.
 */
public class GameSnapshot {

    public long snapshotId;
    public long sequence;
    public long turn;

    public GameState state;

    public GameSnapshot() {
    }

    public GameSnapshot(long snapshotId, long sequence, long turn, GameState state) {
        this.snapshotId = snapshotId;
        this.sequence = sequence;
        this.turn = turn;
        this.state = state;
    }
}
