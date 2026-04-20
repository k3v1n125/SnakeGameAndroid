package org.example.snakegame.ItemFactory;

import org.example.snakegame.Cell;
import org.example.snakegame.Item.Apple;

public class AppleFactory implements ItemFactory<Apple> {
    @Override
    public Apple create(Cell cell, long spawnTimeMillis) {
        return new Apple(cell, spawnTimeMillis);
    }
}
