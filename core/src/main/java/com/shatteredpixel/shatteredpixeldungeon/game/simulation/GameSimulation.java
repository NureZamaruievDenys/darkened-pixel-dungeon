package com.shatteredpixel.shatteredpixeldungeon.game.simulation;

import com.shatteredpixel.shatteredpixeldungeon.game.command.MoveCommand;
import com.shatteredpixel.shatteredpixeldungeon.game.command.PlayerCommand;
import com.shatteredpixel.shatteredpixeldungeon.game.command.WaitCommand;
import com.shatteredpixel.shatteredpixeldungeon.game.delta.EntityDelta;
import com.shatteredpixel.shatteredpixeldungeon.game.delta.StateDelta;
import com.shatteredpixel.shatteredpixeldungeon.game.event.CommandRejectedEvent;
import com.shatteredpixel.shatteredpixeldungeon.game.event.EntityMovedEvent;
import com.shatteredpixel.shatteredpixeldungeon.game.state.GameState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.HeroState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.LevelState;

/**
 * First pure simulation entry point.
 *
 * This is intentionally tiny.
 * The goal is to prove the architecture:
 * command -> validate -> mutate state -> produce delta/events.
 */
public class GameSimulation {

    private final GameState state;

    public GameSimulation(GameState state) {
        this.state = state;
    }

    public SimulationResult apply(PlayerCommand command) {
        if (command == null) {
            return SimulationResult.rejected("Command is null");
        }

        if (command instanceof MoveCommand) {
            return applyMove((MoveCommand) command);
        }

        if (command instanceof WaitCommand) {
            return applyWait((WaitCommand) command);
        }

        return reject(command, "Unsupported command type: " + command.type());
    }

    private SimulationResult applyMove(MoveCommand command) {
        HeroState hero = state.findHero(command.actorEntityId);
        if (hero == null) {
            return reject(command, "Hero not found");
        }

        if (!hero.alive) {
            return reject(command, "Hero is dead");
        }

        LevelState level = state.findLevel(hero.depth, hero.branch);
        if (level == null) {
            return reject(command, "Level not found");
        }

        if (!level.containsCell(command.targetCell)) {
            return reject(command, "Target cell is outside level");
        }

        int from = hero.pos;
        hero.pos = command.targetCell;
        hero.actionTime += 1f;

        StateDelta delta = nextDelta();
        delta.entityDeltas.add(EntityDelta.position(hero.entityId, hero.pos));
        delta.events.add(new EntityMovedEvent(hero.entityId, from, hero.pos));

        return SimulationResult.accepted(delta);
    }

    private SimulationResult applyWait(WaitCommand command) {
        HeroState hero = state.findHero(command.actorEntityId);
        if (hero == null) {
            return reject(command, "Hero not found");
        }

        hero.actionTime += 1f;

        StateDelta delta = nextDelta();
        return SimulationResult.accepted(delta);
    }

    private SimulationResult reject(PlayerCommand command, String reason) {
        StateDelta delta = nextDelta();
        delta.events.add(new CommandRejectedEvent(command.commandId, command.playerId, reason));

        SimulationResult result = SimulationResult.rejected(reason);
        result.delta = delta;
        return result;
    }

    private StateDelta nextDelta() {
        StateDelta delta = new StateDelta();
        delta.baseSequence = state.serverSequence;
        state.serverSequence++;
        delta.sequence = state.serverSequence;
        delta.turn = state.globalTurn;
        return delta;
    }
}