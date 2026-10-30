package com.shatteredpixel.shatteredpixeldungeon.game.save;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.game.state.GameIds;
import com.shatteredpixel.shatteredpixeldungeon.game.state.HeroState;

/**
 * Converts legacy Hero object to pure HeroState.
 */
public class LegacyHeroStateExporter {

    private final LegacyIdRegistry ids;

    public LegacyHeroStateExporter(LegacyIdRegistry ids) {
        this.ids = ids;
    }

    public HeroState export(Hero hero, long ownerPlayerId) {
        if (hero == null) {
            return null;
        }

        HeroState state = new HeroState();

        state.entityId = ids.idFor(hero);
        state.ownerPlayerId = ownerPlayerId;

        state.heroClass = hero.heroClass == null ? null : hero.heroClass.name();
        state.heroSubClass = hero.subClass == null ? null : hero.subClass.name();

        state.depth = Dungeon.depth;
        state.branch = Dungeon.branch;
        state.pos = hero.pos;

        state.hp = hero.HP;
        state.maxHp = hero.HT;

        state.level = hero.lvl;
        state.exp = hero.exp;
        state.strength = hero.STR;

        state.alive = hero.isAlive();
        state.ready = hero.ready;

        state.inventoryId = GameIds.NONE;

        return state;
    }
}