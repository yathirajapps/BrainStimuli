package com.yathirajjp.brainstimuli;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Arrays;
import java.util.Random;

public class Puzzle15 extends AppCompatActivity {

    private AdView mAdView;
    int[][] gameState, lastGameState, completeGameState = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}};
    int noOfMoves, bestMoves;
    TextView scoreText, highScoreText, noOfMovesText, highScoreMsgTextView, bestMovesText;
    SharedPreferences sharedPreferences;
    String itemClicked = "", bestTime = "00:00:00.000";
    Long bestTimeMilliSec, currScoreTime;
    boolean gameInProgress = false;
    Stopwatch stopwatch;
    Handler handler;


    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String timerText = "";
            timerText = "Time\n" + stopwatch.toString();
            scoreText.setText(timerText);

            handler.postDelayed(runnable,10);
        }
    };


    public class MyTouchListener extends OnSwipeTouchListener {

        public MyTouchListener(Context ctx) {
            super(ctx);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            itemClicked = v.getTag().toString();
            return super.onTouch(v, event);
        }

        @Override
        public void onSwipeRight() {
            if (!gameInProgress) {
                gameInProgress = true;
                //Start the timer
                stopwatch.start();
                handler.postDelayed(runnable, 10);
            }

            int row, col;
            row = Integer.parseInt(itemClicked.substring(0,1));
            col = Integer.parseInt(itemClicked.substring(1));

            lastGameState = deepCopy2DArray(gameState);

            for (int i = col+1; i<4; i++){
                if (gameState[row][i] == 0){
                    //Empty tile found, let's move
                    for (int j=i; j> col; j--){
                        gameState[row][j] = gameState[row][j-1];
                    }
                    gameState[row][col] = 0;
                    break;
                }
            }

            if (!Arrays.deepEquals(lastGameState, gameState)) {
                String movesText = "";
                noOfMoves++;
                movesText = "Moves\n" + String.format("%03d", noOfMoves);
                noOfMovesText.setText(movesText);

                loadTiles();

            }

            super.onSwipeRight();
        }

        @Override
        public void onSwipeLeft() {

            if (!gameInProgress) {
                gameInProgress = true;
                //Start the timer
                stopwatch.start();
                handler.postDelayed(runnable, 10);
            }

            int row, col;
            row = Integer.parseInt(itemClicked.substring(0,1));
            col = Integer.parseInt(itemClicked.substring(1));

            lastGameState = deepCopy2DArray(gameState);

            for (int i = col-1; i>=0; i--){
                if (gameState[row][i] == 0){
                    //Empty tile found, let's move
                    for (int j=i; j<col; j++){
                        gameState[row][j] = gameState[row][j+1];
                    }
                    gameState[row][col] = 0;
                    break;
                }
            }

            if (!Arrays.deepEquals(lastGameState, gameState)) {
                String movesText = "";
                noOfMoves++;
                movesText = "Moves\n" + String.format("%03d", noOfMoves);
                noOfMovesText.setText(movesText);

                loadTiles();

            }

            super.onSwipeLeft();
        }

        @Override
        public void onSwipeTop() {
            if (!gameInProgress) {
                gameInProgress = true;
                //Start the timer
                stopwatch.start();
                handler.postDelayed(runnable, 10);
            }

            int row, col;
            row = Integer.parseInt(itemClicked.substring(0,1));
            col = Integer.parseInt(itemClicked.substring(1));

            lastGameState = deepCopy2DArray(gameState);

            for (int i = row-1; i>=0; i--){
                if (gameState[i][col] == 0){
                    //Empty tile found, let's move
                    for (int j=i; j<row; j++){
                        gameState[j][col] = gameState[j+1][col];
                    }
                    gameState[row][col] = 0;
                    break;
                }
            }

            if (!Arrays.deepEquals(lastGameState, gameState)) {
                String movesText = "";
                noOfMoves++;
                movesText = "Moves\n" + String.format("%03d", noOfMoves);
                noOfMovesText.setText(movesText);

                loadTiles();

            }

            super.onSwipeTop();
        }

        @Override
        public void onSwipeBottom() {
            if (!gameInProgress) {
                gameInProgress = true;
                //Start the timer
                stopwatch.start();
                handler.postDelayed(runnable, 10);
            }

            int row, col;
            row = Integer.parseInt(itemClicked.substring(0,1));
            col = Integer.parseInt(itemClicked.substring(1));

            lastGameState = deepCopy2DArray(gameState);

            for (int i = row+1; i<4; i++){
                if (gameState[i][col] == 0){
                    //Empty tile found, let's move
                    for (int j=i; j>row; j--){
                        gameState[j][col] = gameState[j-1][col];
                    }
                    gameState[row][col] = 0;
                    break;
                }
            }

            if (!Arrays.deepEquals(lastGameState, gameState)) {
                String movesText = "";
                noOfMoves++;
                movesText = "Moves\n" + String.format("%03d", noOfMoves);
                noOfMovesText.setText(movesText);

                loadTiles();

            }

            super.onSwipeBottom();
        }
    }


    // This procedure will check if the Game is complete
    public boolean isGameComplete(){
        if (Arrays.deepEquals(gameState, completeGameState)) {
            stopwatch.stop();
            handler.removeCallbacks(runnable);
            gameInProgress=false;
            return true;
        }
        else
            return false;
    }


    // This procedure will deep copy a 2D array
    public int[][] deepCopy2DArray(int[][] input){
        if (input == null)
            return null;

        int [][] result = new int[input.length][];
        for (int r=0; r < input.length; r++){
            result[r] = input[r].clone();

        }
        return result;
    }


    public void showCustomDialog(final String pHeading, String pScore) {
        final Dialog customDialog = new Dialog(Puzzle15.this);
        customDialog.setContentView(R.layout.custom_dialog_layout);

        TextView headerTextView = customDialog.findViewById(R.id.headerTextView);
        TextView yourScoreTextView = customDialog.findViewById(R.id.yourScoreTextView);
        Button menuButton = customDialog.findViewById(R.id.menuButton);
        Button continueButton = customDialog.findViewById(R.id.continueButton);
        Button leaderBoardButton = customDialog.findViewById(R.id.leaderboardButton);

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

                startGame();


            }
        });

        //Method for LeadersBoard click
        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Puzzle15.this, "Sorry, Leadersboard is still under development.", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException e){
            Log.i("CustomDialog", e.getMessage());
        }

        customDialog.setCanceledOnTouchOutside(false);
        customDialog.setCancelable(false);
        customDialog.show();

    }

    //This procedure will load the tiles as per the values in gameState array
    public void loadTiles(){
        int imageResId, drawableResId;
        ImageView imageView;

        for (int i=0; i<4; i++){
            for (int j=0; j<4; j++){
                imageResId = getResources().getIdentifier("image" + Integer.toString(i) + Integer.toString(j), "id", getPackageName());
                drawableResId = getResources().getIdentifier("number" + Integer.toString(gameState[i][j]), "drawable", getPackageName());

                imageView = findViewById(imageResId);

                imageView.setImageResource(drawableResId);

            }
        }


        //Check if the Game is complete by forming a 2048 tile
        if (isGameComplete()){


            String currTime = scoreText.getText().toString().split("\n")[1];
            String[] timeParts = currTime.split(":");
            String[] secMilliSec = timeParts[2].split("\\.");

            currScoreTime = (Long.parseLong(timeParts[0]) * 60 * 60) + // Hours to Seconds
                    (Long.parseLong(timeParts[1]) * 60) +  // Minutes to Seconds
                    Long.parseLong(secMilliSec[0]); // Seconds
            //Seconds to Milliseconds
            currScoreTime *= 1000;
            currScoreTime += Long.parseLong(secMilliSec[1]);

            if (currScoreTime < bestTimeMilliSec || bestTimeMilliSec <= 0){
                bestTimeMilliSec = currScoreTime;
                // Save the new best time and the moves.
                sharedPreferences.edit().putString("Puzzle15BestTime", currTime).apply();
                sharedPreferences.edit().putInt("Puzzle15BestMoves", noOfMoves).apply();

                //Display the best stats
                highScoreText.setText("Time\n" + currTime);
                bestMovesText.setText("Moves\n" + noOfMoves);

            }

            // Show the custom dialog
            if (highScoreMsgTextView.getVisibility() == View.VISIBLE) {
                showCustomDialog("Game Complete!", "New Best Time: " + currTime);
            } else {
                showCustomDialog("Game Complete!", "Time: " + currTime);
            }
        }
    } // End of loadTiles


    public void startGame(){

        scoreText.setText("Score\n00:00:00.000");

        noOfMoves = 0;
        noOfMovesText.setText("Moves\n" + String.format("%03d", noOfMoves));

        highScoreMsgTextView.setVisibility(View.INVISIBLE);

        //Initialize the game array
        for (int i=0; i < 4; i++){
            for (int j=0; j < 4; j++){
                gameState[i][j] = 0;
            }
        }

        int row, col;
        Random random = new Random();

        for (int num=1; num<=15; num++){

            row = random.nextInt(4);
            col = random.nextInt(4);
            while (gameState[row][col] != 0){
                row = random.nextInt(4);
                col = random.nextInt(4);
            }

            gameState[row][col] = num;
        }

        //Load the tiles as per gameState array
        loadTiles();


    }

    public void restartGame(View view){

        stopwatch.stop();
        handler.removeCallbacks(runnable);
        gameInProgress=false;

        startGame();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle15);


        scoreText = (TextView)findViewById(R.id.textViewCurrScoreValue);
        highScoreText = (TextView)findViewById(R.id.textViewHSValue);
        noOfMovesText = (TextView)findViewById(R.id.textViewNoOfMoves);
        bestMovesText = (TextView)findViewById(R.id.textViewBestMoves);
        highScoreMsgTextView = (TextView)findViewById(R.id.textViewHighScoreMsg);
        sharedPreferences = getSharedPreferences("com.yathirajjp.brainstimuli", MODE_PRIVATE);
        handler = new Handler();
        stopwatch = new Stopwatch();

        gameState = new int[4][4];
        lastGameState = new int[4][4];

        Intent intent = getIntent();
        bestMoves = intent.getIntExtra("Puzzle15BestMoves", 0);
        bestTime = intent.getStringExtra("Puzzle15BestTime");

        //Convert the best time in string to milliseconds
        String[] timeParts = bestTime.split(":");
        String[] secMilliSec = timeParts[2].split("\\.");

        bestTimeMilliSec = (Long.parseLong(timeParts[0]) * 60 * 60) + // Hours to Seconds
                (Long.parseLong(timeParts[1]) * 60) +  // Minutes to Seconds
                Long.parseLong(secMilliSec[0]); // Seconds
        //Seconds to Milliseconds
        bestTimeMilliSec *= 1000;

        bestTimeMilliSec += Long.parseLong(secMilliSec[1]);

        startGame();

        // Set the OnTouchListener for all the 16 tiles
        MyTouchListener imageTouchListener = new MyTouchListener(Puzzle15.this);
        int imageResId;
        ImageView imageView;

        for (int i=0; i<4; i++){
            for (int j=0; j<4; j++){
                imageResId = getResources().getIdentifier("image" + Integer.toString(i) + Integer.toString(j), "id", getPackageName());

                imageView = (ImageView)findViewById(imageResId);
                imageView.setOnTouchListener(imageTouchListener);

            }
        }

        //Initialize the mobile ads SDK
        MobileAds.initialize(Puzzle15.this, String.valueOf(R.string.ad_app_id));
        mAdView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  // AdRequest.DEVICE_ID_EMULATOR)    // Test Device: 7378D97884419E089614BB536911AA73
                .build();

        //Start loading the add in the background
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onBackPressed() {

        stopwatch.stop();
        handler.removeCallbacks(runnable);

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
