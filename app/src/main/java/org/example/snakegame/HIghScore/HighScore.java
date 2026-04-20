package org.example.snakegame.HIghScore;

public class HighScore {
    private int bestApples;
    private long bestSurvivalSeconds;
    private long fastestAppleSeconds;

    public HighScore() {
        this(0, 0, Long.MAX_VALUE);
    }

    public HighScore(int bestApples, long bestSurvivalSeconds, long fastestAppleSeconds) {
        this.bestApples = bestApples;
        this.bestSurvivalSeconds = bestSurvivalSeconds;
        this.fastestAppleSeconds = fastestAppleSeconds;
    }

    public int getBestApples() {
        return bestApples;
    }

    public long getBestSurvivalSeconds() {
        return bestSurvivalSeconds;
    }

    public long getFastestAppleSeconds() {
        return fastestAppleSeconds;
    }

    public boolean update(int apples, long survivalSeconds, long fastestAppleSecondsInGame) {
        boolean beaten = false;

        if (apples > bestApples) {
            bestApples = apples;
            beaten = true;
        }
        if (survivalSeconds > bestSurvivalSeconds) {
            bestSurvivalSeconds = survivalSeconds;
            beaten = true;
        }
        if (fastestAppleSecondsInGame < fastestAppleSeconds) {
            fastestAppleSeconds = fastestAppleSecondsInGame;
            beaten = true;
        }

        return beaten;
    }
}
