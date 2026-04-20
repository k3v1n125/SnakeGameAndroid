package org.example.snakegame;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.example.snakegame.Achievement.Achievement;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int ACHIEVEMENT_ENABLED_COLOR = Color.parseColor("#D6FF6B");
    private static final int ACHIEVEMENT_DISABLED_COLOR = Color.parseColor("#6B7A35");

    private GameView gameView;
    private TextView applesText;
    private TextView starsText;
    private TextView lengthText;
    private TextView bestApplesText;
    private TextView bestTimeText;
    private TextView bestFastAppleText;
    private TextView achievementsText;
    private TextView extraLivesText;
    private Button pauseButton;
    private boolean endStateToastShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.game_view);
        applesText = findViewById(R.id.apples_text);
        starsText = findViewById(R.id.stars_text);
        lengthText = findViewById(R.id.length_text);
        bestApplesText = findViewById(R.id.best_apples_text);
        bestTimeText = findViewById(R.id.best_time_text);
        bestFastAppleText = findViewById(R.id.best_fastest_apple_text);
        achievementsText = findViewById(R.id.achievements_text);
        extraLivesText = findViewById(R.id.extra_lives_text);

        gameView.setGameListener(new GameListener() {
            @Override
            public void onStatsUpdated(GameStats stats) {
                applesText.setText(getString(R.string.apples_collected_template, stats.getApplesCollected()));
                starsText.setText(getString(R.string.stars_collected_template, stats.getStarsCollected()));
                lengthText.setText(getString(R.string.snake_length_template, stats.getSnakeLength()));
                extraLivesText.setText(getString(R.string.extra_lives_template, stats.getExtraLives()));
                bestApplesText.setText(getString(R.string.best_apples_template, stats.getBestApples()));
                bestTimeText.setText(getString(R.string.best_time_template, stats.getBestSurvivalSeconds()));

                String fastestAppleText = stats.getFastestAppleSeconds() == Long.MAX_VALUE
                        ? getString(R.string.no_record_text)
                        : stats.getFastestAppleSeconds() + "s";
                bestFastAppleText.setText(getString(R.string.fastest_apple_template, fastestAppleText));
                achievementsText.setText(getString(
                        R.string.achievements_template,
                        stats.getUnlockedAchievements(),
                        stats.getTotalAchievements()
                ));
                updateAchievementsAccess(stats);

                if (!stats.isStarted()) {
                    pauseButton.setText(R.string.start_text);
                } else if (stats.isPaused()) {
                    pauseButton.setText(R.string.resume_text);
                } else {
                    pauseButton.setText(R.string.pause_text);
                }

                if (stats.isGameOver()) {
                    pauseButton.setText(R.string.restart_text);
                    if (!endStateToastShown) {
                        Toast.makeText(
                                MainActivity.this,
                                stats.isGameWon() ? getString(R.string.you_win_text) : getString(R.string.game_over_text),
                                Toast.LENGTH_SHORT
                        ).show();
                        endStateToastShown = true;
                    }
                } else {
                    endStateToastShown = false;
                }
            }

            @Override
            public void onAchievementUnlocked(Achievement achievement) {
                String message = getString(R.string.achievement_unlocked_template, achievement.getTitle());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        Button upButton = findViewById(R.id.button_up);
        Button leftButton = findViewById(R.id.button_left);
        Button downButton = findViewById(R.id.button_down);
        Button rightButton = findViewById(R.id.button_right);
        pauseButton = findViewById(R.id.button_pause);

        upButton.setOnClickListener(v -> gameView.setDirection(GameView.Direction.UP));
        leftButton.setOnClickListener(v -> gameView.setDirection(GameView.Direction.LEFT));
        downButton.setOnClickListener(v -> gameView.setDirection(GameView.Direction.DOWN));
        rightButton.setOnClickListener(v -> gameView.setDirection(GameView.Direction.RIGHT));
        pauseButton.setOnClickListener(v -> gameView.togglePause());

        achievementsText.setOnClickListener(v -> showAchievementsDialog());
        TextPaint achievementsPaint = achievementsText.getPaint();
        achievementsPaint.setUnderlineText(true);
        achievementsText.setEnabled(false);
        achievementsText.setTextColor(ACHIEVEMENT_DISABLED_COLOR);
    }

    private void updateAchievementsAccess(GameStats stats) {
        boolean canOpenAchievements = !stats.isStarted() || stats.isPaused() || stats.isGameOver();
        achievementsText.setEnabled(canOpenAchievements);
        achievementsText.setClickable(canOpenAchievements);
        achievementsText.setAlpha(canOpenAchievements ? 1f : 0.6f);
        achievementsText.setTextColor(canOpenAchievements ? ACHIEVEMENT_ENABLED_COLOR : ACHIEVEMENT_DISABLED_COLOR);
    }

    private void showAchievementsDialog() {
        if (!achievementsText.isEnabled()) {
            return;
        }
        Set<Achievement> unlocked = gameView.getUnlockedAchievements();
        SpannableStringBuilder messageBuilder = new SpannableStringBuilder();

        for (Achievement achievement : Achievement.values()) {
            boolean isUnlocked = unlocked.contains(achievement);
            int titleStart = messageBuilder.length();
            messageBuilder
                    .append(achievement.getTitle())
                    .append("\n")
                    .append(achievement.getDescription())
                    .append("\n\n");
            int titleEnd = titleStart + achievement.getTitle().length();

            messageBuilder.setSpan(
                new ForegroundColorSpan(isUnlocked ? Color.GREEN : Color.RED),
                titleStart,
                titleEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            messageBuilder.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD),
                titleStart,
                titleEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.achievements_dialog_title))
            .setMessage(messageBuilder)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
