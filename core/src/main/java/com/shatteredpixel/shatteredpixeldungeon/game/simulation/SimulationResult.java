package com.shatteredpixel.shatteredpixeldungeon.game.simulation;

import com.shatteredpixel.shatteredpixeldungeon.game.delta.StateDelta;

/**
 * Result of applying one command or one simulation step.
 */
public class SimulationResult {

    public boolean accepted;
    public String rejectionReason;

    public StateDelta delta = new StateDelta();

    public SimulationResult() {
    }

    public static SimulationResult accepted(StateDelta delta) {
        SimulationResult result = new SimulationResult();
        result.accepted = true;
        result.delta = delta;
        return result;
    }

    public static SimulationResult rejected(String reason) {
        SimulationResult result = new SimulationResult();
        result.accepted = false;
        result.rejectionReason = reason;
        return result;
    }
}