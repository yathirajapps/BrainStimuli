package com.yathirajjp.brainstimuli;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class QuickMath extends AppCompatActivity {

    int minRandom = 5, maxRandom = 25, maxSecondsPerQuestion = 5;
    int correctAnsLoc, score = 0, highScore = 0;
    Random random = new Random();
    ArrayList<String> answers = new ArrayList<>();
    ArrayList<String> operators = new ArrayList<>();
    TextView questionView, scoreText, timerText, highScoreMsgTextView;
    Button button1, button2, button3, button4;
    CountDownTimer waitTimer;
    SeekBar timerSeekbar;
    SharedPreferences sharedPreferences;

    public int getRandom(int minimum, int maximum) {
        return random.nextInt(maximum - minimum + 1) + minimum;
    }

    //Check if the number is already part of answers array
    public boolean existsInAnswer(int num) {

        if (answers == null) {
            return false;
        } else {
            for (int i = 0; i < answers.size(); i++) {
                if (Integer.toString(num).equals(answers.get(i))) {
                    return true;
                }

            }
        }

        return false;
    }


    public void generateQuestion () {
        int aNum = getRandom(minRandom, maxRandom), bNum = getRandom(minRandom, maxRandom);
        int choosenOperator = random.nextInt(3);  // 0 -> Addition,  1 -> Subtraction,  2 -> Multiplication
        int maskLocation = random.nextInt(3); // 0 -> A number,  1 -> B number,  2 -> Answer
        int incorrectAnswer, correctAnswer = -1;
        String question = "";

        correctAnsLoc = random.nextInt(4);
        answers.clear();

        if (operators.get(choosenOperator).equals("+")){
            if (maskLocation == 0){
                // Mask the number A in the question
                question = "?" + " + " + Integer.toString(bNum) + " = " + Integer.toString(aNum + bNum);
                correctAnswer = aNum;
            } else if (maskLocation == 1) {
                // Mask the number B in the question
                question = Integer.toString(aNum) + " + ? = " + Integer.toString(aNum + bNum);
                correctAnswer = bNum;
            } else if (maskLocation == 2) {
                // Mask the answer in the question
                question = Integer.toString(aNum) + " + " + Integer.toString(bNum) + " = ?";
                correctAnswer = aNum + bNum;
            }

            for (int i=0; i < 4; i++){
                if (i == correctAnsLoc) {

                    answers.add(Integer.toString(correctAnswer));

                } else {

                    incorrectAnswer = random.nextInt(maxRandom * 2);

                    while (incorrectAnswer == correctAnswer || existsInAnswer(incorrectAnswer)) {
                        incorrectAnswer = random.nextInt(maxRandom * 2);
                    }
                    answers.add(Integer.toString(incorrectAnswer));

                }
            }
        } else if (operators.get(choosenOperator).equals("-")){

            // Swap the numbers if bNum > aNum
            if (bNum > aNum) {
                aNum = aNum + bNum;
                bNum = aNum - bNum;
                aNum = aNum - bNum;
            }

            if (maskLocation == 0) {
                // Mask the number A in the question
                question = "?" + " - " + Integer.toString(bNum) + " = " + Integer.toString(aNum - bNum);
                correctAnswer = aNum;
            }else if (maskLocation == 1) {
                // Mask the number B in the question
                question = Integer.toString(aNum) + " - ? = " + Integer.toString(aNum - bNum);
                correctAnswer = bNum;
            }else if (maskLocation == 2) {
                // Mask the answer in the question
                question = Integer.toString(aNum) + " - " + Integer.toString(bNum) + " = ?";
                correctAnswer = aNum - bNum;
            }

            for (int i=0; i < 4; i++){
                if (i == correctAnsLoc) {

                    answers.add(Integer.toString(correctAnswer));

                } else {

                    incorrectAnswer = random.nextInt(maxRandom);

                    while (incorrectAnswer == correctAnswer || existsInAnswer(incorrectAnswer)) {
                        incorrectAnswer = random.nextInt(maxRandom);
                    }
                    answers.add(Integer.toString(incorrectAnswer));

                }
            }
        } else if (operators.get(choosenOperator).equals("*")){
            while (aNum ==0) { aNum = getRandom(minRandom, maxRandom);}
            while(bNum == 0 || bNum == aNum) { bNum = getRandom(minRandom, maxRandom);}

            if (maskLocation == 0){
                // Mask the number A in the question
                question = "?" + " * " + Integer.toString(bNum) + " = " + Integer.toString(aNum * bNum);
                correctAnswer = aNum;
            } else if (maskLocation == 1) {
                // Mask the number B in the question
                question = Integer.toString(aNum) + " * ? = " + Integer.toString(aNum * bNum);
                correctAnswer = bNum;
            } else if (maskLocation == 2) {
                // Mask the answer in the question
                question = Integer.toString(aNum) + " * " + Integer.toString(bNum) + " = ?";
                correctAnswer = aNum * bNum;
            }

            int tempRand;
            if (aNum > bNum) { tempRand = aNum; } else { tempRand = bNum; }

            for (int i=0; i < 4; i++){
                if (i == correctAnsLoc) {

                    answers.add(Integer.toString(correctAnswer));

                } else {

                    incorrectAnswer = random.nextInt(tempRand * tempRand);

                    while (incorrectAnswer == correctAnswer || ((incorrectAnswer % 10) != (correctAnswer % 10)) || existsInAnswer(incorrectAnswer)) {
                        incorrectAnswer = random.nextInt(tempRand * tempRand);
                    }
                    answers.add(Integer.toString(incorrectAnswer));

                }
            }
        }


        // Display the question & and the answers
        questionView.setText(question);

        button1.setText(answers.get(0));
        button2.setText(answers.get(1));
        button3.setText(answers.get(2));
        button4.setText(answers.get(3));
    } // End of generateQuestion

    public void showCustomDialog(String pHeading, String pScore) {
        final Dialog customDialog = new Dialog(QuickMath.this);
        customDialog.setContentView(R.layout.custom_dialog_layout);

        TextView headerTextView = (TextView)customDialog.findViewById(R.id.headerTextView);
        TextView yourScoreTextView = (TextView)customDialog.findViewById(R.id.yourScoreTextView);
        Button menuButton = (Button)customDialog.findViewById(R.id.menuButton);
        Button continueButton = (Button)customDialog.findViewById(R.id.continueButton);
        Button leaderBoardButton = (Button)customDialog.findViewById(R.id.leaderboardButton);

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
                startQuickMath();

            }
        });

        //Method for LeadersBoard click
        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QuickMath.this, "Sorry, Leadersboard is still under development.", Toast.LENGTH_SHORT).show();
            }
        });

        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.setCancelable(false);
        customDialog.show();

    }

    public void checkAnswer(View view) {

        if (view.getTag().equals(Integer.toString(correctAnsLoc))){

            score ++;
            scoreText.setText(Integer.toString(score));
            waitTimer.cancel();

            //Set the background to Gree to show it is right answer
            view.setBackground(getResources().getDrawable(R.drawable.round_btn_green));
            CountDownTimer pauseScreen = new CountDownTimer(500,600) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    startQuickMath();
                }
            }.start();


            if (score > highScore){
                //New High Score, store it

                sharedPreferences.edit().putInt("QuickMathHighScore", score).apply();
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

        }else {
            //Wrong answer.  Stop the quiz and popup the result

            waitTimer.cancel();

            //Set the background to Red to show it is wrong answer
            view.setBackground(getResources().getDrawable(R.drawable.round_btn_red));

            CountDownTimer pauseScreen = new CountDownTimer(500,600) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    //Call the custom dialog here
                    if (highScoreMsgTextView.getVisibility() == View.VISIBLE) {
                        showCustomDialog("Game Over!", "New High Score: " + Integer.toString(score));
                    } else {
                        showCustomDialog("Game Over!", "Your Score: " + Integer.toString(score));
                    }
                }
            }.start();
        }

    } // End of checkAnswer

    private void resetAnswerButtons(){

        button1.setBackground(getResources().getDrawable(R.drawable.round_btn));
        button2.setBackground(getResources().getDrawable(R.drawable.round_btn));
        button3.setBackground(getResources().getDrawable(R.drawable.round_btn));
        button4.setBackground(getResources().getDrawable(R.drawable.round_btn));

    } // End of resetAnswerButtons

    public void startQuickMath() {

        resetAnswerButtons();
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
        setContentView(R.layout.activity_quick_math);


        questionView = (TextView) findViewById(R.id.questionTextView);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
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

        //#e56ab3
//mySeekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(srcColor, PorterDuff.Mode.MULTIPLY));
        // Load the supported operators
        operators.add("+");
        operators.add("-");
        operators.add("*");

        timerSeekbar.getThumb().mutate().setAlpha(0);

        Intent intent = getIntent();

        highScore = intent.getIntExtra("QuickMathHighScore", 0);

        startQuickMath();

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
//        return super.onOptionsItemSelected(item);
    }
}
