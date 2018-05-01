package feis_clan.points;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class Statistics extends Activity{
    private int gamesPlayed = 0;
    private int redWins = 0;
    private int blueWins = 0;
    private int draws = 0;
    private int redCaptured = 0;
    private int blueCaptured = 0;
    private int maxScore = 0;
    private int maxCapturedPerGame = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        getStats();

        TextView gamesPlayedView = findViewById(R.id.total_games_played);
        TextView redWinsView = findViewById(R.id.red_wins);
        TextView blueWinsView = findViewById(R.id.blue_wins);
        TextView drawsView = findViewById(R.id.draw);
        TextView redCapturedView = findViewById(R.id.red_captured);
        TextView blueCapturedView = findViewById(R.id.blue_captured);
        TextView maxScoreView = findViewById(R.id.max_score);
        TextView maxCapturedView = findViewById(R.id.max_captured);

        gamesPlayedView.setText(String.format(Locale.getDefault(), "%d", gamesPlayed));
        redWinsView.setText(String.format(Locale.getDefault(), "%d", redWins));
        blueWinsView.setText(String.format(Locale.getDefault(), "%d", blueWins));
        drawsView.setText(String.format(Locale.getDefault(), "%d", draws));
        redCapturedView.setText(String.format(Locale.getDefault(), "%d", redCaptured));
        blueCapturedView.setText(String.format(Locale.getDefault(), "%d", blueCaptured));
        maxScoreView.setText(String.format(Locale.getDefault(), "%d", maxScore));
        maxCapturedView.setText(String.format(Locale.getDefault(), "%d", maxCapturedPerGame));
    }

    private void getStats(){
        Context context = getApplicationContext();
        SharedPreferences preferences = context.getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        gamesPlayed = preferences.getInt("GAMES_PLAYED", 0);
        redWins = preferences.getInt("RED_WINS", 0);
        blueWins = preferences.getInt("BLUE_WINS", 0);
        draws = preferences.getInt("DRAWS", 0);
        redCaptured = preferences.getInt("RED_CAPTURED", 0);
        blueCaptured = preferences.getInt("BLUE_CAPTURED", 0);
        maxScore = preferences.getInt("MAX_SCORE", 0);
        maxCapturedPerGame = preferences.getInt("MAX_CAPTURED", 0);
    }
}
