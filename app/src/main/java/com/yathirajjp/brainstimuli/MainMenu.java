package com.yathirajjp.brainstimuli;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.w3c.dom.Text;

public class MainMenu extends AppCompatActivity {

    TextView quickMathHighScoreText, trueFalseHighScoreText, game2048HighScoreText, game2048MovesText;
    TextView puzzle15HighScoreText, puzzle15MovesText;
    int quickMathHighScore = 0, trueFalseHighScore = 0, game2048HighScore, game2048BestMoves, puzzle15BestMoves;
    String puzzle15BestTime;

    public void showQuickMath(View view) {

        Log.i("Info", "Clicked the Relative Layout of Quick Math");

        Intent quickMathIntent = new Intent(getApplicationContext(), QuickMath.class);
        quickMathIntent.putExtra("QuickMathHighScore", quickMathHighScore);
        startActivity(quickMathIntent);
        finish();
    }

    public void showTrueFalse(View view) {

        Log.i("Info", "Clicked the Relative Layout of True/False");
        Intent trueFalseIntent = new Intent(getApplicationContext(), TrueFalse.class);
        trueFalseIntent.putExtra("TrueFalseHighScore", quickMathHighScore);
        startActivity(trueFalseIntent);
        finish();
    }

    public void showGame2048(View view){
        Log.i("Info", "Clicked on Relative Layout of Game 2048");
        Log.i("LayoutChosen", view.getTag().toString());
        Intent game2048Intent = new Intent(getApplicationContext(), Game2048.class);
        game2048Intent.putExtra("Game2048HighScore", game2048HighScore);
        game2048Intent.putExtra("Game2048BestMoves", game2048BestMoves);
        startActivity(game2048Intent);
        finish();
    }

    public void showPuzzle15(View view){
        Log.i("Info", "Clicked on Relative Layout of Puzzle15");
        Intent puzzle15Intent = new Intent(getApplicationContext(), Puzzle15.class);
        puzzle15Intent.putExtra("Puzzle15BestTime", puzzle15BestTime);
        puzzle15Intent.putExtra("Puzzle15BestMoves", puzzle15BestMoves);
        startActivity(puzzle15Intent);
        finish();
    }

    public void rateTheApp(View view) {

        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        RelativeLayout quickMathLayout = (RelativeLayout)findViewById(R.id.quickMathRelLayout);
        RelativeLayout trueFalseLayout = (RelativeLayout)findViewById(R.id.trueFalseRelLayout);
        RelativeLayout game2048Layout = (RelativeLayout)findViewById(R.id.game2048RelLayout);
        RelativeLayout puzzle15Layout = (RelativeLayout)findViewById(R.id.puzzle15RelLayout);

        quickMathLayout.setTranslationX(2000.0f);
        trueFalseLayout.setTranslationX(2000.0f);
        game2048Layout.setTranslationX(2000.0f);
        puzzle15Layout.setTranslationX(2000.0f);

        quickMathLayout.animate()
                .translationXBy(-2000.0f)
                .setDuration(800)
                .start();

        trueFalseLayout.animate()
                .translationXBy(-2000.0f)
                .setDuration(1200)
                .start();

        game2048Layout.animate()
                .translationXBy(-2000.0f)
                .setDuration(1600)
                .start();

        puzzle15Layout.animate()
                .translationXBy(-2000.0f)
                .setDuration(2000)
                .start();

        setResult(Activity.RESULT_OK);


        quickMathHighScoreText = (TextView) findViewById(R.id.quickMathScore);
        trueFalseHighScoreText = (TextView) findViewById(R.id.trueFalseScore);
        game2048HighScoreText = (TextView) findViewById(R.id.game2048Score);
        game2048MovesText = (TextView)findViewById(R.id.game2048Moves);
        puzzle15HighScoreText = (TextView)findViewById(R.id.puzzle15Score);
        puzzle15MovesText = (TextView)findViewById(R.id.puzzle15Moves);

        SharedPreferences sharedPreferences = getSharedPreferences("com.yathirajjp.brainstimuli", MODE_PRIVATE);

        quickMathHighScore = sharedPreferences.getInt("QuickMathHighScore", 0);
        quickMathHighScoreText.setText("High Score: " + Integer.toString(quickMathHighScore));

        trueFalseHighScore = sharedPreferences.getInt("TrueFalseHighScore", 0);
        trueFalseHighScoreText.setText("High Score: " + Integer.toString(trueFalseHighScore));

        game2048HighScore = sharedPreferences.getInt("Game2048HighScore", 0);
        game2048HighScoreText.setText("High Score: " + Integer.toString(game2048HighScore));
        game2048BestMoves = sharedPreferences.getInt("Game2048BestMoves", 0);
        game2048MovesText.setText("Moves: " + Integer.toString(game2048BestMoves));

        puzzle15BestTime = sharedPreferences.getString("Puzzle15BestTime", "00:00:00.000");
        puzzle15HighScoreText.setText("Best Time: " + puzzle15BestTime);
        puzzle15BestMoves = sharedPreferences.getInt("Puzzle15BestMoves", 0);
        puzzle15MovesText.setText("Moves: " + Integer.toString(puzzle15BestMoves));

    }
}
