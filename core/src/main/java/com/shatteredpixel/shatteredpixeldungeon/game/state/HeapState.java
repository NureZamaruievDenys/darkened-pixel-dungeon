package com.shatteredpixel.shatteredpixeldungeon.game.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure floor item heap state.
 */
public class HeapState {

    public long heapId;

    public int pos;

    public final List<ItemState> items = new ArrayList<>();

    public HeapState() {
    }
}