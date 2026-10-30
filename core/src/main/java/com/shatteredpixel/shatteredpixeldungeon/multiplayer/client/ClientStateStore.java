package com.shatteredpixel.shatteredpixeldungeon.multiplayer.client;

import com.shatteredpixel.shatteredpixeldungeon.game.delta.EntityDelta;
import com.shatteredpixel.shatteredpixeldungeon.game.delta.StateDelta;
import com.shatteredpixel.shatteredpixeldungeon.game.snapshot.GameSnapshot;
import com.shatteredpixel.shatteredpixeldungeon.game.state.GameState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.HeroState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.LevelState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.MobState;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.delta.StateDeltaDto;
import com.shatteredpixel.shatteredpixeldungeon.network.dto.snapshot.GameSnapshotDto;
import com.shatteredpixel.shatteredpixeldungeon.network.mapper.DeltaDtoMapper;
import com.shatteredpixel.shatteredpixeldungeon.network.mapper.SnapshotDtoMapper;

/**
 * Client-side store of authoritative state received from server.
 *
 * Current version:
 * - applies full snapshots
 * - applies basic entity deltas: pos/hp/alive
 * - does not update real Dungeon yet
 */
public class ClientStateStore {

    private final SnapshotDtoMapper snapshotMapper = new SnapshotDtoMapper();
    private final DeltaDtoMapper deltaMapper = new DeltaDtoMapper();

    private GameState state;

    private long snapshotId;
    private long serverSequence;
    private long turn;

    public void applySnapshot(GameSnapshotDto dto) {
        GameSnapshot snapshot = snapshotMapper.fromDto(dto);
        if (snapshot == null) {
            return;
        }

        this.snapshotId = snapshot.snapshotId;
        this.serverSequence = snapshot.sequence;
        this.turn = snapshot.turn;
        this.state = snapshot.state;
    }

    public void applyDelta(StateDeltaDto dto) {
        StateDelta delta = deltaMapper.fromDto(dto);
        if (delta == null || state == null) {
            return;
        }

        for (EntityDelta entityDelta : delta.entityDeltas) {
            applyEntityDelta(entityDelta);
        }

        this.serverSequence = delta.sequence;
        this.turn = delta.turn;

        if (state != null) {
            state.serverSequence = delta.sequence;
            state.globalTurn = delta.turn;
        }
    }

    private void applyEntityDelta(EntityDelta delta) {
        HeroState hero = state.findHero(delta.entityId);
        if (hero != null) {
            applyHeroDelta(hero, delta);
            return;
        }

        MobState mob = findMob(delta.entityId);
        if (mob != null) {
            applyMobDelta(mob, delta);
        }
    }

    private void applyHeroDelta(HeroState hero, EntityDelta delta) {
        if (delta.pos != null) {
            hero.pos = delta.pos;
        }

        if (delta.hp != null) {
            hero.hp = delta.hp;
        }

        if (delta.alive != null) {
            hero.alive = delta.alive;
        }
    }

    private void applyMobDelta(MobState mob, EntityDelta delta) {
        if (delta.pos != null) {
            mob.pos = delta.pos;
        }

        if (delta.hp != null) {
            mob.hp = delta.hp;
        }

        if (delta.alive != null) {
            mob.alive = delta.alive;
        }
    }

    private MobState findMob(long entityId) {
        if (state == null) {
            return null;
        }

        for (LevelState level : state.levels) {
            for (MobState mob : level.mobs) {
                if (mob.entityId == entityId) {
                    return mob;
                }
            }
        }

        return null;
    }

    public GameState state() {
        return state;
    }

    public long snapshotId() {
        return snapshotId;
    }

    public long serverSequence() {
        return serverSequence;
    }

    public long turn() {
        return turn;
    }

    public boolean hasState() {
        return state != null;
    }
}