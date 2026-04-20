package org.example.snakegame.HIghScore;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreManager {
    private static final String PREFS_NAME = "snake_high_score";
    private static final String KEY_BEST_APPLES = "bestApples";
    private static final String KEY_BEST_SURVIVAL = "bestSurvival";
    private static final String KEY_FASTEST_APPLE = "fastestApple";

    private final SharedPreferences preferences;
    private final HighScore record;

    public HighScoreManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        record = load();
    }

    public HighScore getRecord() {
        return record;
    }

    public boolean update(int apples, long survivalSeconds, long fastestAppleSeconds) {
        boolean beaten = record.update(apples, survivalSeconds, fastestAppleSeconds);
        if (beaten) {
            save();
        }
        return beaten;
    }

    private HighScore load() {
        int bestApples = preferences.getInt(KEY_BEST_APPLES, 0);
        long bestSurvival = preferences.getLong(KEY_BEST_SURVIVAL, 0L);
        long fastestApple = preferences.getLong(KEY_FASTEST_APPLE, Long.MAX_VALUE);
        return new HighScore(bestApples, bestSurvival, fastestApple);
    }

    private void save() {
        preferences.edit()
                .putInt(KEY_BEST_APPLES, record.getBestApples())
                .putLong(KEY_BEST_SURVIVAL, record.getBestSurvivalSeconds())
                .putLong(KEY_FASTEST_APPLE, record.getFastestAppleSeconds())
                .apply();
    }
}
