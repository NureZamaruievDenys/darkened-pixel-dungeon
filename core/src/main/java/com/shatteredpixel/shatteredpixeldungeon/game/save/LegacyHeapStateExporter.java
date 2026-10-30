package com.shatteredpixel.shatteredpixeldungeon.game.save;

import com.shatteredpixel.shatteredpixeldungeon.game.state.HeapState;
import com.shatteredpixel.shatteredpixeldungeon.game.state.ItemState;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

/**
 * Converts legacy Heap object to pure HeapState.
 */
public class LegacyHeapStateExporter {

    private final LegacyIdRegistry ids;

    public LegacyHeapStateExporter(LegacyIdRegistry ids) {
        this.ids = ids;
    }

    public HeapState export(Heap heap) {
        if (heap == null) {
            return null;
        }

        HeapState state = new HeapState();

        state.heapId = ids.idFor(heap);
        state.pos = heap.pos;

        if (heap.items != null) {
            for (Item item : heap.items) {
                ItemState itemState = exportItem(item);
                if (itemState != null) {
                    state.items.add(itemState);
                }
            }
        }

        return state;
    }

    private ItemState exportItem(Item item) {
        if (item == null) {
            return null;
        }

        ItemState state = new ItemState();

        state.itemId = ids.idFor(item);
        state.itemType = item.getClass().getName();

        state.quantity = item.quantity();
        state.level = item.level();

        state.cursed = item.cursed;
        state.identified = item.isIdentified();

        return state;
    }
}