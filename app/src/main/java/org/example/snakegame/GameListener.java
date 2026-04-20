package org.example.snakegame;

import org.example.snakegame.Achievement.Achievement;

public interface GameListener {
    void onStatsUpdated(GameStats stats);

    void onAchievementUnlocked(Achievement achievement);
}
