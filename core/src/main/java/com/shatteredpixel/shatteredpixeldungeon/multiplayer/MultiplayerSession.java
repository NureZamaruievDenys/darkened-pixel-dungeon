package com.shatteredpixel.shatteredpixeldungeon.multiplayer;

import com.shatteredpixel.shatteredpixeldungeon.game.state.GameState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.PlayerState;

import java.util.ArrayList;
import java.util.List;

/**
 * Server-side multiplayer session state.
 */
public class MultiplayerSession {

    public long sessionId;

    public GameState gameState;

    public final List<PlayerSession> players = new ArrayList<>();

    public MultiplayerSession() {
    }

    public PlayerSession findPlayer(long playerId) {
        for (PlayerSession player : players) {
            if (player.playerId == playerId) {
                return player;
            }
        }
        return null;
    }

    public PlayerSession addPlayerFromState(PlayerState state) {
        if (state == null) {
            return null;
        }

        PlayerSession existing = findPlayer(state.playerId);
        if (existing != null) {
            return existing;
        }

        PlayerSession session = new PlayerSession(state.playerId, state.name, state.host);
        session.controlledHeroId = state.controlledHeroId;
        session.lastClientSequence = state.lastClientSequence;
        session.lastAcknowledgedServerSequence = state.lastAcknowledgedServerSequence;

        players.add(session);
        return session;
    }
}