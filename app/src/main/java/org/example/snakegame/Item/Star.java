package org.example.snakegame.Item;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.example.snakegame.Cell;

import java.io.IOException;
import java.io.InputStream;

public class Star extends Item {
    private static final long EXPIRE_DURATION_MILLIS = 6000;
    private static final long STAGE2_MILLIS = 2000;
    private static final long STAGE3_MILLIS = 4000;

    private static Bitmap bitmap1;
    private static Bitmap bitmap2;
    private static Bitmap bitmap3;

    public Star(Cell cell, long spawnTimeMillis) {
        super(cell, spawnTimeMillis);
    }

    @Override
    public Bitmap getBitmap(Context context) {
        loadBitmaps(context);
        long elapsed = System.currentTimeMillis() - getSpawnTimeMillis();
        if (elapsed >= STAGE3_MILLIS) return bitmap3;
        if (elapsed >= STAGE2_MILLIS) return bitmap2;
        return bitmap1;
    }

    private static void loadBitmaps(Context context) {
        if (bitmap1 != null) return;
        AssetManager assets = context.getAssets();
        bitmap1 = decode(assets, "star/1.png");
        bitmap2 = decode(assets, "star/2.png");
        bitmap3 = decode(assets, "star/3.png");
    }

    private static Bitmap decode(AssetManager assets, String path) {
        try (InputStream is = assets.open(path)) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getExpireDurationMillis() {
        return EXPIRE_DURATION_MILLIS;
    }
}
