package org.example.snakegame.Achievement;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class AchievementManager {
    private static final String PREFS_NAME = "snake_achievements";

    private final SharedPreferences preferences;
    private final Set<Achievement> unlocked = EnumSet.noneOf(Achievement.class);

    private int consecutiveApplesCollected = 0;
    private int consecutiveStarsCollected = 0;

    public AchievementManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        load();
    }

    public Set<Achievement> getUnlocked() {
        return Collections.unmodifiableSet(unlocked);
    }

    public boolean isUnlocked(Achievement achievement) {
        return unlocked.contains(achievement);
    }

    public List<Achievement> checkStats(int apples, int snakeLength, long elapsedSeconds) {
        List<Achievement> unlockedNow = new ArrayList<>();
        addIfUnlocked(unlockedNow, apples >= 1, Achievement.FIRST_APPLE);
        addIfUnlocked(unlockedNow, apples >= 10, Achievement.TEN_APPLES);
        addIfUnlocked(unlockedNow, apples >= 20, Achievement.TWENTY_APPLES);
        addIfUnlocked(unlockedNow, snakeLength >= 10, Achievement.LENGTH_TEN);
        addIfUnlocked(unlockedNow, snakeLength >= 20, Achievement.LENGTH_TWENTY);
        return unlockedNow;
    }

    public List<Achievement> onGameEnded(int apples, long elapsedSeconds) {
        List<Achievement> unlockedNow = new ArrayList<>();
        addIfUnlocked(unlockedNow, elapsedSeconds >= 30, Achievement.SURVIVE_30);
        addIfUnlocked(unlockedNow, elapsedSeconds >= 60, Achievement.SURVIVE_60);
        addIfUnlocked(unlockedNow, elapsedSeconds >= 120, Achievement.SURVIVE_120);
        addIfUnlocked(unlockedNow, elapsedSeconds >= 60 && apples == 0, Achievement.SURVIVE_60_NO_APPLE);
        addIfUnlocked(unlockedNow, elapsedSeconds >= 120 && apples == 0, Achievement.SURVIVE_120_NO_APPLE);
        return unlockedNow;
    }

    public List<Achievement> onAppleCollected(long secondsTaken) {
        consecutiveApplesCollected += 1;
        List<Achievement> unlockedNow = new ArrayList<>();
        addIfUnlocked(unlockedNow, secondsTaken < 2, Achievement.SPEED_DEMON);
        addIfUnlocked(unlockedNow, consecutiveApplesCollected >= 5, Achievement.PERFECTIONIST);
        return unlockedNow;
    }

    public void onAppleMissed() {
        consecutiveApplesCollected = 0;
    }

    public List<Achievement> onStarCollected() {
        consecutiveStarsCollected += 1;
        List<Achievement> unlockedNow = new ArrayList<>();
        addIfUnlocked(unlockedNow, consecutiveStarsCollected >= 5, Achievement.STAR_KEEPER);
        return unlockedNow;
    }

    public void onStarMissed() {
        consecutiveStarsCollected = 0;
    }

    public List<Achievement> onGameWon() {
        List<Achievement> unlockedNow = new ArrayList<>();
        addIfUnlocked(unlockedNow, true, Achievement.BOARD_FILLER);
        return unlockedNow;
    }

    public void resetGameCounters() {
        consecutiveApplesCollected = 0;
        consecutiveStarsCollected = 0;
    }

    private Achievement tryUnlock(boolean condition, Achievement achievement) {
        if (!condition || unlocked.contains(achievement)) {
            return null;
        }
        unlocked.add(achievement);
        save();
        return achievement;
    }

    private void addIfUnlocked(List<Achievement> unlockedNow, boolean condition, Achievement achievement) {
        Achievement unlockedAchievement = tryUnlock(condition, achievement);
        if (unlockedAchievement != null) {
            unlockedNow.add(unlockedAchievement);
        }
    }

    private void load() {
        for (Achievement achievement : Achievement.values()) {
            if (preferences.getBoolean(achievement.name(), false)) {
                unlocked.add(achievement);
            }
        }
    }

    private void save() {
        SharedPreferences.Editor editor = preferences.edit();
        for (Achievement achievement : Achievement.values()) {
            editor.putBoolean(achievement.name(), unlocked.contains(achievement));
        }
        editor.apply();
    }
}
