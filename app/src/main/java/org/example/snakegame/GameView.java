package org.example.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import org.example.snakegame.Achievement.Achievement;
import org.example.snakegame.Achievement.AchievementManager;
import org.example.snakegame.HIghScore.HighScore;
import org.example.snakegame.HIghScore.HighScoreManager;
import org.example.snakegame.Item.Apple;
import org.example.snakegame.Item.Item;
import org.example.snakegame.Item.Star;
import org.example.snakegame.ItemFactory.AppleFactory;
import org.example.snakegame.ItemFactory.StarFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameView extends View {
    private static final int GRID_SIZE = 20;
    private static final int UPDATE_DELAY = 120;
    private static final int MAX_APPLES_LIMIT = 7;

    private final Paint boardPaint = new Paint();
    private final Paint borderPaint = new Paint();
    private final Paint snakePaint = new Paint();
    private final Paint headPaint = new Paint();
    private final Paint applePaint = new Paint();
    private final Paint starPaint = new Paint();
    private final Paint textPaint = new Paint();
    
    private Bitmap appleBitmap;
    private Bitmap starBitmap;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final AppleFactory appleFactory = new AppleFactory();
    private final StarFactory starFactory = new StarFactory();

    private final List<Cell> snake = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private int maxApples = 1;
    private int totalStarsTowardNextApple = 0;
    private boolean isGrowing = false;
    private Direction direction = Direction.RIGHT;

    private int cellSize;
    private int boardWidth;
    private int boardHeight;

    private long startTime;
    private long pausedTime;
    private long pausedDuration;

    private int extraLifeUsed;
    private final GameStats gameStats = new GameStats();

    private AchievementManager achievementManager;
    private HighScoreManager highScoreManager;

    private boolean endStateHandled = false;

    private boolean dirChanged = false;

    private GameListener listener;
    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            if (!gameStats.isGameOver()) {
                updateGame();
                invalidate();
                handler.postDelayed(this, UPDATE_DELAY);
            }
        }
    };

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        boardPaint.setColor(Color.BLACK);
        borderPaint.setColor(Color.GREEN);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4f);
        snakePaint.setColor(Color.GREEN);
        headPaint.setColor(Color.RED);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40f);
        textPaint.setAntiAlias(true);

        achievementManager = new AchievementManager(getContext());
        highScoreManager = new HighScoreManager(getContext());
        gameStats.setUnlockedAchievements(achievementManager.getUnlocked().size());
        gameStats.setTotalAchievements(Achievement.values().length);
        
        // Load apple image
        appleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star);
    }

    public void setGameListener(GameListener listener) {
        this.listener = listener;
    }

    public Set<Achievement> getUnlockedAchievements() {
        return achievementManager.getUnlocked();
    }

    public void restartGame() {
        resetGame(true);
        startLoop();
        notifyStats();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = Math.min(w, h) / GRID_SIZE;
        boardWidth = cellSize * GRID_SIZE;
        boardHeight = cellSize * GRID_SIZE;
        resetGame(false);
        invalidate();
    }

    private void resetGame(boolean startImmediately) {
        handler.removeCallbacks(gameLoop);
        snake.clear();
        items.clear();
        int startX = GRID_SIZE / 2;
        int startY = GRID_SIZE / 2;
        snake.add(new Cell(startX, startY));
        snake.add(new Cell(startX - 1, startY));
        snake.add(new Cell(startX - 2, startY));
        direction = Direction.RIGHT;
        gameStats.setStarted(startImmediately);
        gameStats.setGameOver(false);
        gameStats.setGameWon(false);
        gameStats.setPaused(false);
        isGrowing = false;
        gameStats.setApplesCollected(0);
        gameStats.setStarsCollected(0);
        totalStarsTowardNextApple = 0;
        extraLifeUsed = 0;
        gameStats.setCurrentFastestAppleSeconds(Long.MAX_VALUE);
        endStateHandled = false;
        startTime = startImmediately ? System.currentTimeMillis() : 0L;
        pausedDuration = 0;
        maxApples = 1;
        achievementManager.resetGameCounters();
        gameStats.setUnlockedAchievements(achievementManager.getUnlocked().size());
        spawnApple();
        notifyStats();
    }

    public void startGame() {
        if (gameStats.isStarted() || gameStats.isGameOver()) {
            return;
        }

        long startTimestamp = System.currentTimeMillis();
        gameStats.setStarted(true);
        gameStats.setPaused(false);
        startTime = startTimestamp;
        pausedDuration = 0;
        for (Item item : items) {
            item.setSpawnTimeMillis(startTimestamp);
        }
        startLoop();
        notifyStats();
        invalidate();
    }

    private void startLoop() {
        handler.removeCallbacks(gameLoop);
        handler.postDelayed(gameLoop, UPDATE_DELAY);
    }

    public void togglePause() {
        if (gameStats.isGameOver()) {
            restartGame();
            return;
        }
        if (!gameStats.isStarted()) {
            startGame();
            return;
        }
        if (gameStats.isPaused()) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    public void pauseGame() {
        if (!gameStats.isPaused() && !gameStats.isGameOver()) {
            gameStats.setPaused(true);
            pausedTime = System.currentTimeMillis();
            handler.removeCallbacks(gameLoop);
            notifyStats();
            invalidate();
        }
    }

    public void resumeGame() {
        if (gameStats.isPaused() && !gameStats.isGameOver()) {
            gameStats.setPaused(false);
            long resumeTime = System.currentTimeMillis();
            long pauseDelta = resumeTime - pausedTime;
            pausedDuration += pauseDelta;
            for (Item item : items) {
                item.shiftSpawnTime(pauseDelta);
            }
            startLoop();
            notifyStats();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, boardWidth, boardHeight, boardPaint);

        if (gameStats.isStarted()) {
            for (Item item : items) {
                if (item instanceof Apple) {
                    drawBitmap(canvas, item.getCell(), appleBitmap);
                } else if (item instanceof Star) {
                    drawBitmap(canvas, item.getCell(), starBitmap);
                }
            }

            for (int i = 0; i < snake.size(); i++) {
                drawCell(canvas, snake.get(i), i == 0 ? headPaint : snakePaint);
            }
        }

        // Draw border on top of game elements
        canvas.drawRect(0, 0, boardWidth, boardHeight, borderPaint);

        if (gameStats.isPaused()) {
            String message = "PAUSED";
            float textWidth = textPaint.measureText(message);
            canvas.drawText(message, (boardWidth - textWidth) / 2f, boardHeight / 2f, textPaint);
        } else if (!gameStats.isStarted()) {
            String message = "PRESS START";
            float textWidth = textPaint.measureText(message);
            canvas.drawText(message, (boardWidth - textWidth) / 2f, boardHeight / 2f, textPaint);
        }

        String durationMessage = getDurationText();
        if (gameStats.isGameOver()) {
            durationMessage = durationMessage + (gameStats.isGameWon() ? ", You Win" : ", Game Over");
        }
        canvas.drawText(durationMessage, 20, boardHeight + 60, textPaint);
    }

    private void drawCell(Canvas canvas, Cell cell, Paint paint) {
        int left = cell.x * cellSize;
        int top = cell.y * cellSize;
        canvas.drawRect(left, top, left + cellSize, top + cellSize, paint);
    }

    private void drawBitmap(Canvas canvas, Cell cell, Bitmap bitmap) {
        if (bitmap == null) return;
        int left = cell.x * cellSize;
        int top = cell.y * cellSize;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, cellSize, cellSize, true);
        canvas.drawBitmap(scaledBitmap, left, top, null);
    }

    private void updateGame() {
        if (gameStats.isGameOver() || gameStats.isPaused() || !gameStats.isStarted()) {
            return;
        }

        // Check if items have expired and respawn them
        long currentTime = System.currentTimeMillis();
        for (int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if ((currentTime - item.getSpawnTimeMillis()) > item.getExpireDurationMillis()) {
                items.remove(i);
                if (item instanceof Apple) {
                    achievementManager.onAppleMissed();
                } else if (item instanceof Star) {
                    achievementManager.onStarMissed();
                    spawnStar();
                }
            }
        }

        // Ensure we always have apples available
        while (countApples() < maxApples) {
            spawnApple();
        }

        Cell head = snake.get(0);
        Cell next;
        switch (direction) {
            case UP:
                next = new Cell(head.x, head.y - 1);
                break;
            case DOWN:
                next = new Cell(head.x, head.y + 1);
                break;
            case LEFT:
                next = new Cell(head.x - 1, head.y);
                break;
            default:
                next = new Cell(head.x + 1, head.y);
                break;
        }
        dirChanged = false;

        checkCollision(next);
        if (gameStats.isGameOver()) {
            handleGameEndIfNeeded();
            notifyStats();
            return;
        }

        snake.add(0, next);

        // Check item collision
        for (int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if (!next.equals(item.getCell())) {
                continue;
            }

            if (item instanceof Apple) {
                gameStats.setApplesCollected(gameStats.getApplesCollected() + 1);
                isGrowing = true;
                long secondsTaken = (currentTime - item.getSpawnTimeMillis()) / 1000;
                if (secondsTaken < gameStats.getCurrentFastestAppleSeconds()) {
                    gameStats.setCurrentFastestAppleSeconds(secondsTaken);
                }
                notifyAchievements(achievementManager.onAppleCollected(secondsTaken));
                items.remove(i);
                if (gameStats.getApplesCollected() >= 4 && !hasStar()) {
                    spawnStar();
                }
                break;
            }

            if (item instanceof Star) {
                gameStats.setStarsCollected(gameStats.getStarsCollected() + 1);
                totalStarsTowardNextApple += 1;
                notifyAchievements(achievementManager.onStarCollected());
                if (totalStarsTowardNextApple >= 5 && maxApples < MAX_APPLES_LIMIT) {
                    maxApples += 1;
                    totalStarsTowardNextApple = 0;
                }
                items.remove(i);
                spawnStar();
                break;
            }
        }

        if (!isGrowing && snake.size() > 0) {
            snake.remove(snake.size() - 1);
        }

        if (snake.size() >= GRID_SIZE * GRID_SIZE) {
            gameStats.setGameWon(true);
            gameStats.setGameOver(true);
            items.clear();
            notifyAchievements(achievementManager.onGameWon());
        }

        isGrowing = false;
        notifyAchievements(achievementManager.checkStats(gameStats.getApplesCollected(), getSnakeLength(), getElapsedSeconds()));
        handleGameEndIfNeeded();
        notifyStats();
    }

    private int countApples() {
        int appleCount = 0;
        for (Item item : items) {
            if (item instanceof Apple) {
                appleCount += 1;
            }
        }
        return appleCount;
    }

    private boolean hasStar() {
        for (Item item : items) {
            if (item instanceof Star) {
                return true;
            }
        }
        return false;
    }

    private void removeStarIfPresent() {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i) instanceof Star) {
                items.remove(i);
                return;
            }
        }
    }

    private void checkCollision(Cell next) {
        if (next.x < 0 || next.x >= GRID_SIZE || next.y < 0 || next.y >= GRID_SIZE) {
            gameStats.setGameOver(true);
            return;
        }
        for (Cell segment : snake) {
            if (segment.equals(next)) {
                if (getExtraLives() > 0) {
                    extraLifeUsed += 1;
                } else {
                    gameStats.setGameOver(true);
                    notifyStats();
                    return;
                }
                return;
            }
        }
        notifyStats();
    }

    private void spawnApple() {
        Cell appleCell = createRandomCell();
        if (appleCell != null) {
            items.add(appleFactory.create(appleCell, System.currentTimeMillis()));
        }
    }

    private void spawnStar() {
        removeStarIfPresent();
        Cell starCell = createRandomCell();
        if (starCell == null) {
            return;
        }
        items.add(starFactory.create(starCell, System.currentTimeMillis()));
    }

    private Cell createRandomCell() {
        if (snake.size() + items.size() >= GRID_SIZE * GRID_SIZE) {
            return null;
        }

        Cell cell;
        do {
            int x = random.nextInt(GRID_SIZE);
            int y = random.nextInt(GRID_SIZE);
            cell = new Cell(x, y);
        } while (snake.contains(cell) || containsItemCell(cell));

        return cell;
    }

    private boolean containsItemCell(Cell cell) {
        for (Item item : items) {
            if (cell.equals(item.getCell())) {
                return true;
            }
        }
        return false;
    }

    public void setDirection(Direction newDirection) {
        if (gameStats.isGameOver() || dirChanged) {
            return;
        }

        if ((direction == Direction.UP && newDirection == Direction.DOWN)
                || (direction == Direction.DOWN && newDirection == Direction.UP)
                || (direction == Direction.LEFT && newDirection == Direction.RIGHT)
                || (direction == Direction.RIGHT && newDirection == Direction.LEFT)) {
            return;
        }

        direction = newDirection;
        dirChanged = true;
        if (gameStats.isPaused() && gameStats.isStarted()) {
            resumeGame();
        }
    }

    private int getSnakeLength() {
        return snake.size();
    }

    private int getExtraLives() {
        return Math.max(0, gameStats.getStarsCollected() / 5 - extraLifeUsed);
    }

    private long getElapsedSeconds() {
        if (!gameStats.isStarted()) {
            return 0;
        }
        return (System.currentTimeMillis() - startTime - pausedDuration) / 1000;
    }

    private String getDurationText() {
        long elapsed = getElapsedSeconds();
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        if (minutes == 0) {
            return seconds + " s";
        }
        return minutes + " m " + seconds + " s";
    }

    private void notifyAchievements(List<Achievement> unlockedNow) {
        if (unlockedNow == null || unlockedNow.isEmpty()) {
            return;
        }
        gameStats.setUnlockedAchievements(achievementManager.getUnlocked().size());
        if (listener != null) {
            for (Achievement achievement : unlockedNow) {
                listener.onAchievementUnlocked(achievement);
            }
        }
    }

    private void handleGameEndIfNeeded() {
        if (!gameStats.isGameOver() || endStateHandled) {
            return;
        }
        endStateHandled = true;
        long elapsedSeconds = getElapsedSeconds();
        notifyAchievements(achievementManager.onGameEnded(gameStats.getApplesCollected(), elapsedSeconds));
        highScoreManager.update(gameStats.getApplesCollected(), elapsedSeconds, gameStats.getCurrentFastestAppleSeconds());
    }

    private void notifyStats() {
        if (listener != null) {
            HighScore highScore = highScoreManager.getRecord();
            gameStats.setSnakeLength(getSnakeLength());
            gameStats.setExtraLives(getExtraLives());
            gameStats.setDurationText(getDurationText());
            gameStats.setBestApples(highScore.getBestApples());
            gameStats.setBestSurvivalSeconds(highScore.getBestSurvivalSeconds());
            gameStats.setFastestAppleSeconds(highScore.getFastestAppleSeconds());
            listener.onStatsUpdated(gameStats);
        }
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
