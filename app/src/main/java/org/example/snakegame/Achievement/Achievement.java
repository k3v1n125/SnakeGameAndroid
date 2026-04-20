package org.example.snakegame.Achievement;

public enum Achievement {
    FIRST_APPLE("First Bite", "Collect your first apple"),
    TEN_APPLES("Apple Hoarder", "Collect 10 apples in one game"),
    TWENTY_APPLES("Glutton", "Collect 20 apples in one game"),
    SPEED_DEMON("Speed Demon", "Collect an apple in under 2 seconds"),
    PERFECTIONIST("Perfectionist", "Collect 5 apples without missing one"),
    STAR_KEEPER("Star Keeper", "Collect 5 stars without letting any disappear"),
    SURVIVE_30("Survivor", "Survive for 30 seconds"),
    SURVIVE_60("Veteran", "Survive for 60 seconds"),
    SURVIVE_120("Legend", "Survive for 120 seconds"),
    LENGTH_TEN("Growing Up", "Reach a snake length of 10"),
    LENGTH_TWENTY("Long Boy", "Reach a snake length of 20"),
    SURVIVE_60_NO_APPLE("Apple Hater", "Survive for 60 seconds with no apples collected"),
    SURVIVE_120_NO_APPLE("Stay Short", "Survive for 120 seconds with no apples collected"),
    BOARD_FILLER("Board Filler", "Fill the entire board with the snake");

    private final String title;
    private final String description;

    Achievement(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
