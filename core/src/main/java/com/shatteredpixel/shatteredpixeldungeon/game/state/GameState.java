package com.shatteredpixel.shatteredpixeldungeon.game.state;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    public long gameId;

    public long seed;

    public int depth;
    public int branch;

    public long globalTurn;
    public long serverSequence;

    public final List<PlayerState> players = new ArrayList<>();
    public final List<HeroState> heroes = new ArrayList<>();
    public final List<LevelState> levels = new ArrayList<>();

    public GameState() {
    }

    public PlayerState findPlayer(long playerId) {
        for (PlayerState player : players) {
            if (player.playerId == playerId) {
                return player;
            }
        }
        return null;
    }

    public HeroState findHero(long heroId) {
        for (HeroState hero : heroes) {
            if (hero.entityId == heroId) {
                return hero;
            }
        }
        return null;
    }

    public LevelState findLevel(int depth, int branch) {
        for (LevelState level : levels) {
            if (level.depth == depth && level.branch == branch) {
                return level;
            }
        }
        return null;
    }
}
