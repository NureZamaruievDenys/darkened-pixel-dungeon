package com.shatteredpixel.shatteredpixeldungeon.game.save;

import com.shatteredpixel.shatteredpixeldungeon.game.state.GameIds;

import java.util.IdentityHashMap;

/**
 * Assigns temporary stable ids to legacy runtime objects.
 *
 * Important:
 * - ids are stable only while the current Java process is alive
 * - this is enough for early multiplayer prototyping
 * - later persistent ids should be stored in game state / bundles
 */
public class LegacyIdRegistry {

    private final IdentityHashMap<Object, Long> ids = new IdentityHashMap<>();

    private long nextId = 1L;

    public long idFor(Object object) {
        if (object == null) {
            return GameIds.NONE;
        }

        Long existing = ids.get(object);
        if (existing != null) {
            return existing;
        }

        long id = nextId++;
        ids.put(object, id);
        return id;
    }

    public void clear() {
        ids.clear();
        nextId = 1L;
    }
}