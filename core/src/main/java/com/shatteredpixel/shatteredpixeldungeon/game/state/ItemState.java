package com.shatteredpixel.shatteredpixeldungeon.game.state;

public class ItemState {

    public long itemId;

    public String itemType;

    public int quantity = 1;
    public int level;

    public boolean cursed;
    public boolean identified;

    public ItemState() {
    }
}
