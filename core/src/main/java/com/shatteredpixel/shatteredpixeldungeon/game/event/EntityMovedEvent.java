package com.shatteredpixel.shatteredpixeldungeon.game.event;

/**
 * Entity movement event.
 */
public class EntityMovedEvent extends GameEvent {

    public long entityId;

    public int fromCell;
    public int toCell;

    public EntityMovedEvent() {
    }

    public EntityMovedEvent(long entityId, int fromCell, int toCell) {
        this.entityId = entityId;
        this.fromCell = fromCell;
        this.toCell = toCell;
    }

    @Override
    public String type() {
        return "ENTITY_MOVED";
    }
}