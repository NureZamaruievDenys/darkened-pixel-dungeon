package com.shatteredpixel.shatteredpixeldungeon.game.save;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.game.state.GameState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.HeroState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.LevelState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.PlayerState;

/**
 * Exports current legacy Dungeon singleton into pure GameState.
 *
 * This is the bridge between current singleplayer architecture
 * and future multiplayer server-authoritative architecture.
 */
public class LegacyDungeonStateExporter {

    private final LegacyIdRegistry ids = new LegacyIdRegistry();

    private final LegacyHeroStateExporter heroExporter = new LegacyHeroStateExporter(ids);
    private final LegacyLevelStateExporter levelExporter = new LegacyLevelStateExporter(ids);

    public GameState exportLocalGame(long localPlayerId, String playerName) {
        GameState state = new GameState();

        state.gameId = Dungeon.seed;
        state.seed = Dungeon.seed;

        state.depth = Dungeon.depth;
        state.branch = Dungeon.branch;

        PlayerState player = new PlayerState();
        player.playerId = localPlayerId;
        player.name = playerName;
        player.host = true;
        player.currentDepth = Dungeon.depth;
        player.currentBranch = Dungeon.branch;
        player.status = PlayerState.Status.PLAYING;

        HeroState hero = heroExporter.export(Dungeon.hero, localPlayerId);
        if (hero != null) {
            player.controlledHeroId = hero.entityId;
            state.heroes.add(hero);
        }

        LevelState level = levelExporter.export(Dungeon.level);
        if (level != null) {
            state.levels.add(level);
        }

        state.players.add(player);

        return state;
    }

    public void clearIds() {
        ids.clear();
    }
}