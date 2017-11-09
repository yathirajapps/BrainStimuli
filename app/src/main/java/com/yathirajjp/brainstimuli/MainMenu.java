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

public class MainMenu extends AppCompatActivity {
    TextView quickMathHighScoreText, trueFalseHighScoreText;
    int quickMathHighScore = 0, trueFalseHighScore = 0;

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

        quickMathLayout.setTranslationX(1000.0f);
        trueFalseLayout.setTranslationX(1000.0f);

        quickMathLayout.animate()
                .translationXBy(-1000.0f)
                .setDuration(800)
                .start();

        trueFalseLayout.animate()
                .translationXBy(-1000.0f)
                .setDuration(1200)
                .start();

        setResult(Activity.RESULT_OK);


        quickMathHighScoreText = (TextView) findViewById(R.id.quickMathScore);
        trueFalseHighScoreText = (TextView) findViewById(R.id.trueFalseScore);

        SharedPreferences sharedPreferences = getSharedPreferences("com.yathirajjp.brainstimuli", MODE_PRIVATE);

        quickMathHighScore = sharedPreferences.getInt("QuickMathHighScore", 0);
        quickMathHighScoreText.setText("High Score: " + Integer.toString(quickMathHighScore));

        trueFalseHighScore = sharedPreferences.getInt("TrueFalseHighScore", 0);
        trueFalseHighScoreText.setText("High Score: " + Integer.toString(trueFalseHighScore));

    }
}
