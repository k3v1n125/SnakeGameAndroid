package org.example.snakegame.Item;

import android.content.Context;
import android.graphics.Bitmap;

import org.example.snakegame.Cell;

public abstract class Item {
    private final Cell cell;
    private long spawnTimeMillis;

    protected Item(Cell cell, long spawnTimeMillis) {
        this.cell = cell;
        this.spawnTimeMillis = spawnTimeMillis;
    }

    public Cell getCell() {
        return cell;
    }

    public long getSpawnTimeMillis() {
        return spawnTimeMillis;
    }

    public void setSpawnTimeMillis(long spawnTimeMillis) {
        this.spawnTimeMillis = spawnTimeMillis;
    }

    public void shiftSpawnTime(long deltaMillis) {
        spawnTimeMillis += deltaMillis;
    }

    public abstract Bitmap getBitmap(Context context);

    public abstract long getExpireDurationMillis();
}
