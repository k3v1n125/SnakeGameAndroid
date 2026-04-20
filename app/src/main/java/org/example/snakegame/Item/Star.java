package org.example.snakegame.Item;

import org.example.snakegame.Cell;

public class Star extends Item {
    private static final long EXPIRE_DURATION_MILLIS = 5000;

    public Star(Cell cell, long spawnTimeMillis) {
        super(cell, spawnTimeMillis);
    }

    @Override
    public long getExpireDurationMillis() {
        return EXPIRE_DURATION_MILLIS;
    }
}
