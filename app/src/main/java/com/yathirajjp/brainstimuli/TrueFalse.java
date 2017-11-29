package com.yathirajjp.brainstimuli;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class TrueFalse extends AppCompatActivity {


    int minRandom = 0, maxRandom = 25, maxSecondsPerQuestion = 5;
    Random random = new Random();
    ArrayList<String> operators = new ArrayList<>();
    TextView questionView;
    int score = 0;
    TextView scoreText, timerText, highScoreMsgTextView;
    CountDownTimer waitTimer;
    SeekBar timerSeekbar;
    int trueFalse;  // 0 -> False,  1 -> True
    SharedPreferences sharedPreferences;
    int highScore = 0;


    public int getRandom(int minimum, int maximum) {
        return random.nextInt(maximum - minimum + 1) + minimum;
    }


    public void generateQuestion () {
        int aNum = getRandom(minRandom, maxRandom), bNum = getRandom(minRandom, maxRandom);
        int choosenOperator = random.nextInt(3);  // 0 -> Addition,  1 -> Subtraction,  2 -> Multiplication
        int incorrectAnswer;
        String question = "";

        trueFalse = random.nextInt(2);

        question = Integer.toString(aNum) + " " + operators.get(choosenOperator) + Integer.toString(bNum) + " = ";

        if (operators.get(choosenOperator).equals("+")){

            if (trueFalse == 0){  // Load incorrect answer
                incorrectAnswer = random.nextInt(maxRandom * 2);

                while (incorrectAnswer == (aNum + bNum)){
                    incorrectAnswer = random.nextInt(maxRandom * 2);
                }

                question += Integer.toString(incorrectAnswer);

            } else {

                question += Integer.toString(aNum + bNum);

            }

        } else if (operators.get(choosenOperator).equals("-")){

            // Swap the numbers if bNum > aNum
            if (bNum > aNum) {
                aNum = aNum + bNum;
                bNum = aNum - bNum;
                aNum = aNum - bNum;
            }
            question = Integer.toString(aNum) + " " + operators.get(choosenOperator) + Integer.toString(bNum) + " = ";

            if (trueFalse == 0){  // Load incorrect answer
                incorrectAnswer = random.nextInt(maxRandom * 2);

                while (incorrectAnswer == (aNum - bNum)){
                    incorrectAnswer = random.nextInt(maxRandom * 2);
                }

                question += Integer.toString(incorrectAnswer);

            } else {

                question += Integer.toString(aNum - bNum);

            }

        } else if (operators.get(choosenOperator).equals("*")){

            if (trueFalse == 0){  // Load incorrect answer
                incorrectAnswer = random.nextInt(maxRandom * maxRandom);

                while (incorrectAnswer == (aNum * bNum) || ((incorrectAnswer % 10) != (aNum * bNum) % 10)){
                    incorrectAnswer = random.nextInt(maxRandom * maxRandom);
                }

                question += Integer.toString(incorrectAnswer);

            } else {

                question += Integer.toString(aNum * bNum);

            }

        }


        // Display the question & and the answers
        questionView.setText(question);

    }

    public void showCustomDialog(String pHeading, String pScore) {
        final Dialog customDialog = new Dialog(TrueFalse.this);
        customDialog.setContentView(R.layout.custom_dialog_layout);

        TextView headerTextView = (TextView)customDialog.findViewById(R.id.headerTextView);
        TextView yourScoreTextView = (TextView)customDialog.findViewById(R.id.yourScoreTextView);
        Button menuButton = (Button)customDialog.findViewById(R.id.menuButton);
        Button continueButton = (Button)customDialog.findViewById(R.id.continueButton);

        headerTextView.setText(pHeading);
        yourScoreTextView.setText(pScore);
        //Method for Menu button click
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customDialog.dismiss();
                Intent mainMenuIntent = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(mainMenuIntent);
                finish();
            }
        });

        //Method for Continue button click
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customDialog.dismiss();
                //Reset the score
                score = 0;
                scoreText.setText(Integer.toString(score));
                highScoreMsgTextView.setVisibility(View.INVISIBLE);
                startTrueFalse();

            }
        });

        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.setCancelable(false);
        customDialog.show();

    }

    public void checkAnswer(View view) {

        if (view.getTag().equals(Integer.toString(trueFalse))){
            score ++;
            scoreText.setText(Integer.toString(score));
            waitTimer.cancel();

            if (score > highScore){
                //New High Score, store it
                sharedPreferences.edit().putInt("TrueFalseHighScore", score).apply();
                highScore = score;

                if (highScoreMsgTextView.getVisibility() == View.INVISIBLE){
                    highScoreMsgTextView.setVisibility(View.VISIBLE);

                    int[] originalPos = new int[2];
                    highScoreMsgTextView.getLocationOnScreen(originalPos);

                    Animation animation = new TranslateAnimation(originalPos[0], originalPos[0], 0, 20);
                    animation.setDuration(1000);
                    animation.setRepeatCount(0);

                    highScoreMsgTextView.startAnimation(animation);
                }
            }

            startTrueFalse();
        }else {
            //Wrong answer.  Stop the quiz and popup the result

            waitTimer.cancel();

            //Call the custom dialog here
            if (highScoreMsgTextView.getVisibility() == View.VISIBLE) {
                showCustomDialog("Game Over!", "New High Score: " + Integer.toString(score));
            } else {
                showCustomDialog("Game Over!", "Your Score: " + Integer.toString(score));
            }

        }

    }

    public void startTrueFalse() {
        generateQuestion();

        waitTimer = new CountDownTimer(maxSecondsPerQuestion * 1000, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                String timer = "";
                int seconds = (int) ((millisUntilFinished + 1000) / 1000);
                int minutes = seconds / 60;

                seconds = seconds - (minutes * 60);

                timer = Integer.toString(minutes) + ":" + String.format("%02d", seconds);
                timerText.setText(timer);
                timerSeekbar.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                //Didn't answer within the time.  Stopping the quiz
                timerText.setText("0:00");
                timerSeekbar.setProgress(0);

                //Call the custom dialog here
                if (highScoreMsgTextView.getVisibility() == View.VISIBLE) {
                    showCustomDialog("Game Over!", "New High Score: " + Integer.toString(score));
                } else {
                    showCustomDialog("Game Over!", "Your Score: " + Integer.toString(score));
                }
            }
        }.start();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_false);


        questionView = (TextView) findViewById(R.id.questionTextView);
        scoreText = (TextView)findViewById(R.id.scoreTextView);
        timerText = (TextView)findViewById(R.id.timerTextView);
        timerSeekbar = (SeekBar)findViewById(R.id.timerSeekbar);
        highScoreMsgTextView = (TextView)findViewById(R.id.textViewHighScoreMsg);
        sharedPreferences = getSharedPreferences("com.yathirajjp.brainstimuli", MODE_PRIVATE);

        timerSeekbar.setMax(maxSecondsPerQuestion * 1000);
        timerSeekbar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(ResourcesCompat.getColor(
                getResources(), R.color.appThemeColor, null
        ),
                PorterDuff.Mode.MULTIPLY
        ));

        // Load the supported operators
        operators.add("+");
        operators.add("-");
        operators.add("*");

        timerSeekbar.getThumb().mutate().setAlpha(0);

        highScore = sharedPreferences.getInt("TrueFalseHighScore", 0);

        startTrueFalse();
    }

    @Override
    public void onBackPressed() {
        waitTimer.cancel();

        Intent mainMenuIntent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(mainMenuIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return false;
    }
}
