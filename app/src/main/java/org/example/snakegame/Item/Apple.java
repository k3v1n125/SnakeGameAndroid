package org.example.snakegame.Item;

import org.example.snakegame.Cell;

public class Apple extends Item {
    private static final long EXPIRE_DURATION_MILLIS = 5000;

    public Apple(Cell cell, long spawnTimeMillis) {
        super(cell, spawnTimeMillis);
    }

    @Override
    public long getExpireDurationMillis() {
        return EXPIRE_DURATION_MILLIS;
    }
}
