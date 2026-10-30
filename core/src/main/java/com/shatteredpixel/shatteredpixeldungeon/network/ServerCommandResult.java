package com.shatteredpixel.shatteredpixeldungeon.network;

import com.shatteredpixel.shatteredpixeldungeon.network.dto.delta.StateDeltaDto;

/**
 * Network-facing result of server command processing.
 */
public class ServerCommandResult {

    public boolean accepted;

    public String rejectionReason;

    public StateDeltaDto delta;

    public ServerCommandResult() {
    }

    public static ServerCommandResult accepted(StateDeltaDto delta) {
        ServerCommandResult result = new ServerCommandResult();
        result.accepted = true;
        result.delta = delta;
        return result;
    }

    public static ServerCommandResult rejected(String reason, StateDeltaDto delta) {
        ServerCommandResult result = new ServerCommandResult();
        result.accepted = false;
        result.rejectionReason = reason;
        result.delta = delta;
        return result;
    }
}