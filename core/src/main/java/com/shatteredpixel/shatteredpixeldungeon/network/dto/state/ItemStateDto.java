package com.shatteredpixel.shatteredpixeldungeon.network.dto.state;

public class ItemStateDto {

    public long itemId;

    public String itemType;

    public int quantity;
    public int level;

    public boolean cursed;
    public boolean identified;

    public ItemStateDto() {
    }
}