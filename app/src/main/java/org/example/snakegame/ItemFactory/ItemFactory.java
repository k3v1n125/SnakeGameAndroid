package org.example.snakegame.ItemFactory;

import org.example.snakegame.Cell;
import org.example.snakegame.Item.Item;

public interface ItemFactory<T extends Item> {
    T create(Cell cell, long spawnTimeMillis);
}
