package com.shatteredpixel.shatteredpixeldungeon.game.save;

import com.shatteredpixel.shatteredpixeldungeon.game.snapshot.GameSnapshot;
import com.shatteredpixel.shatteredpixeldungeon.game.state.GameState;

/**
 * Builds initial snapshots from current legacy game.
 */
public class LegacySnapshotBuilder {

    private final LegacyDungeonStateExporter exporter = new LegacyDungeonStateExporter();

    private long nextSnapshotId = 1L;

    public GameSnapshot buildLocalSnapshot(long localPlayerId, String playerName) {
        GameState state = exporter.exportLocalGame(localPlayerId, playerName);

        GameSnapshot snapshot = new GameSnapshot();
        snapshot.snapshotId = nextSnapshotId++;
        snapshot.sequence = state.serverSequence;
        snapshot.turn = state.globalTurn;
        snapshot.state = state;

        return snapshot;
    }
}
