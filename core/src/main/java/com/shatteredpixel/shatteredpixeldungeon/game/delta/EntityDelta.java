package com.shatteredpixel.shatteredpixeldungeon.game.delta;

/**
 * Minimal entity delta for first multiplayer prototype.
 */
public class EntityDelta {

    public long entityId;

    public Integer pos;
    public Integer hp;
    public Boolean alive;

    public EntityDelta() {
    }

    public static EntityDelta position(long entityId, int pos) {
        EntityDelta delta = new EntityDelta();
        delta.entityId = entityId;
        delta.pos = pos;
        return delta;
    }
}
