package org.example.snakegame.ItemFactory;

import org.example.snakegame.Cell;
import org.example.snakegame.Item.Star;

public class StarFactory implements ItemFactory<Star> {
    @Override
    public Star create(Cell cell, long spawnTimeMillis) {
        return new Star(cell, spawnTimeMillis);
    }
}
