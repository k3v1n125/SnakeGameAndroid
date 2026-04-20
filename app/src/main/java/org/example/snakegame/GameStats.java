package org.example.snakegame;

public class GameStats {
    private int snakeLength;
    private int applesCollected;
    private int starsCollected;
    private int extraLives;
    private String durationText = "0 s";
    private boolean gameOver;
    private boolean gameWon;
    private boolean paused;
    private boolean started;
    private int bestApples;
    private long bestSurvivalSeconds;
    private long fastestAppleSeconds = Long.MAX_VALUE;
    private long currentFastestAppleSeconds = Long.MAX_VALUE;
    private int unlockedAchievements;
    private int totalAchievements;

    public int getSnakeLength() {
        return snakeLength;
    }

    public void setSnakeLength(int snakeLength) {
        this.snakeLength = snakeLength;
    }

    public int getApplesCollected() {
        return applesCollected;
    }

    public void setApplesCollected(int applesCollected) {
        this.applesCollected = applesCollected;
    }

    public int getStarsCollected() {
        return starsCollected;
    }

    public void setStarsCollected(int starsCollected) {
        this.starsCollected = starsCollected;
    }

    public int getExtraLives() {
        return extraLives;
    }

    public void setExtraLives(int extraLives) {
        this.extraLives = extraLives;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }

    public int getBestApples() {
        return bestApples;
    }

    public void setBestApples(int bestApples) {
        this.bestApples = bestApples;
    }

    public long getBestSurvivalSeconds() {
        return bestSurvivalSeconds;
    }

    public void setBestSurvivalSeconds(long bestSurvivalSeconds) {
        this.bestSurvivalSeconds = bestSurvivalSeconds;
    }

    public long getFastestAppleSeconds() {
        return fastestAppleSeconds;
    }

    public void setFastestAppleSeconds(long fastestAppleSeconds) {
        this.fastestAppleSeconds = fastestAppleSeconds;
    }

    public long getCurrentFastestAppleSeconds() {
        return currentFastestAppleSeconds;
    }

    public void setCurrentFastestAppleSeconds(long currentFastestAppleSeconds) {
        this.currentFastestAppleSeconds = currentFastestAppleSeconds;
    }

    public int getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public void setUnlockedAchievements(int unlockedAchievements) {
        this.unlockedAchievements = unlockedAchievements;
    }

    public int getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(int totalAchievements) {
        this.totalAchievements = totalAchievements;
    }
}
