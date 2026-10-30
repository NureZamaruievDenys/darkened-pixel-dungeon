package com.shatteredpixel.shatteredpixeldungeon.multiplayer;

import com.shatteredpixel.shatteredpixeldungeon.game.command.PlayerCommand;
import com.shatteredpixel.shatteredpixeldungeon.game.simulation.GameSimulation;
import com.shatteredpixel.shatteredpixeldungeon.game.simulation.SimulationResult;
import com.shatteredpixel.shatteredpixeldungeon.game.state.GameState;

/**
 * Server-authoritative simulation entry point.
 *
 * First implementation:
 * - applies one command immediately
 * - no background thread
 * - no fixed tick
 * - no KryoNet dependency
 */
public class ServerGameLoop {

    private final GameState state;
    private final GameSimulation simulation;

    public ServerGameLoop(GameState state) {
        this.state = state;
        this.simulation = new GameSimulation(state);
    }

    public SimulationResult applyCommand(PlayerCommand command) {
        return simulation.apply(command);
    }

    public GameState state() {
        return state;
    }
}