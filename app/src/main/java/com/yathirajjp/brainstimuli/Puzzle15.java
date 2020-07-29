package com.yathirajjp.brainstimuli;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class Puzzle15 extends AppCompatActivity {

    int[][] gameState, lastGameState, completeGameState = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}};
    int noOfMoves, bestMoves;
    TextView scoreText, highScoreText, noOfMovesText, highScoreMsgTextView, bestMovesText;
    SharedPreferences sharedPreferences;
    String itemClicked = "", bestTime = "00:00:00";
    Long bestTimeMilliSec, currScoreTime;
    boolean gameInProgress = false, loadPrevGame = false;
    Stopwatch stopwatch;
    Handler handler;


    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String timerText = "";
            timerText = "Time\n" + stopwatch.toStringHMS();  //HH:MM:SS format without milliseconds
            scoreText.setText(timerText);

            handler.postDelayed(runnable,500);
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
                handler.postDelayed(runnable, 500);
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
                handler.postDelayed(runnable, 500);
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
                handler.postDelayed(runnable, 500);
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
                handler.postDelayed(runnable, 500);
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

            CountDownTimer pauseNShow = new CountDownTimer(500,600) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {

                    String currTime = scoreText.getText().toString().split("\n")[1];
                    String[] timeParts = currTime.split(":");
                    String[] secMilliSec = timeParts[2].split("\\.");

                    currScoreTime = (Long.parseLong(timeParts[0]) * 60 * 60) + // Hours to Seconds
                            (Long.parseLong(timeParts[1]) * 60) +  // Minutes to Seconds
                            Long.parseLong(secMilliSec[0]); // Seconds
                    //Seconds to Milliseconds
                    currScoreTime *= 1000;
                    //currScoreTime += Long.parseLong(secMilliSec[1]);

                    if (currScoreTime < bestTimeMilliSec || bestTimeMilliSec <= 0){
                        bestTimeMilliSec = currScoreTime;
                        // Save the new best time and the moves.
                        sharedPreferences.edit().putString("Puzzle15BestTime", currTime).apply();
                        sharedPreferences.edit().putInt("Puzzle15BestMoves", noOfMoves).apply();

                        //Display the best stats
                        highScoreText.setText("Time\n" + currTime.split("\\.")[0]); // Display excluding milli seconds
                        bestMovesText.setText("Moves\n" + noOfMoves);

                    }
                    // Show the custom dialog
                    if (highScoreMsgTextView.getVisibility() == View.VISIBLE) {
                        showCustomDialog("Game Complete!", "New Best Time: " + currTime);
                    } else {
                        showCustomDialog("Game Complete!", "Time: " + currTime);
                    }
                }
            }.start();

        }
    } // End of loadTiles


    public void startGame(){

        scoreText.setText("Time\n00:00:00");

        highScoreMsgTextView.setVisibility(View.INVISIBLE);

        if(loadPrevGame) {

            //Load the Previous game state
            for(int row=0; row < 4; row++) {
                for(int col=0; col < 4; col++){
                    gameState[row][col] = sharedPreferences.getInt("P15_" + row + "_" + col, 0);
                }
            }

            noOfMoves = sharedPreferences.getInt("P15_PrevMoves", 0);
            long prevTimeMilliSec = sharedPreferences.getLong("P15_PrevTimeMilliSec",0);

            //Start the timer
            stopwatch.start(prevTimeMilliSec);
            handler.postDelayed(runnable, 500);

            gameInProgress = true;
            loadPrevGame = false;

        }else {
            noOfMoves = 0;

            //Initialize the game array
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    gameState[i][j] = 0;
                }
            }

            int row, col;
            Random random = new Random();

            for (int num = 1; num <= 15; num++) {

                row = random.nextInt(4);
                col = random.nextInt(4);
                while (gameState[row][col] != 0) {
                    row = random.nextInt(4);
                    col = random.nextInt(4);
                }

                gameState[row][col] = num;
            }
        }


        //Below commented code is for testing purpose - to easily finish the game
/*
        int num=1;
        for (int i=0; i < 4; i++){
            for (int j=0; j < 4; j++){
                gameState[i][j] = num++;
            }
        }

        gameState[3][3] = 15;
        gameState[3][2] = 0;

 */

        noOfMovesText.setText("Moves\n" + String.format("%03d", noOfMoves));

        //Load the tiles as per gameState array
        loadTiles();

    } //End of startGame

    public void restartGame(View view){

        AlertDialog.Builder popupMsgBuilder = new AlertDialog.Builder(this);

        popupMsgBuilder.setTitle("Confirm");
        popupMsgBuilder.setMessage("Game in progress. Are you sure to Restart?");

        popupMsgBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                stopwatch.stop();
                //handler.removeCallbacks(runnable);
                gameInProgress=false;

                startGame();
            }
        });

        popupMsgBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Do Nothing
                stopwatch.resume();
                handler.postDelayed(runnable,500);
                dialogInterface.dismiss();
            }
        });

        if (gameInProgress){
            //Pause the clock as we are showing the confirmation pop-up msg
            stopwatch.pause();
            handler.removeCallbacks(runnable);

            AlertDialog popupMsg = popupMsgBuilder.create();

            //Commented code is to restrict clicking outside of AlertDialog
            /*popupMsg.setCanceledOnTouchOutside(false);
            popupMsg.setCancelable(false); */
            popupMsg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    stopwatch.resume();
                    handler.postDelayed(runnable,500);
                }
            });

            popupMsg.show();

        } else {
            stopwatch.stop();
            handler.removeCallbacks(runnable);

            startGame();
        }
    } //End of restartGame


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle15);


        Log.i("Info", "Within onCreate of Puzzle15");
        scoreText = (TextView)findViewById(R.id.textViewCurrScoreValue);
        highScoreText = (TextView)findViewById(R.id.textViewHSValue);
        noOfMovesText = (TextView)findViewById(R.id.textViewNoOfMoves);
        bestMovesText = (TextView)findViewById(R.id.textViewBestMoves);
        highScoreMsgTextView = (TextView)findViewById(R.id.textViewHighScoreMsg);

        Log.i("Info", "After all findViewById");
        sharedPreferences = getSharedPreferences("com.yathirajjp.brainstimuli", MODE_PRIVATE);
        handler = new Handler();
        stopwatch = new Stopwatch();

        gameState = new int[4][4];
        lastGameState = new int[4][4];

        Intent intent = getIntent();
        bestMoves = intent.getIntExtra("Puzzle15BestMoves", 0);
        bestTime = intent.getStringExtra("Puzzle15BestTime");

        highScoreText.setText("Time\n" + bestTime.split("\\.")[0]); //Display excluding the milli Seconds
        bestMovesText.setText("Moves\n" + bestMoves);

        //Convert the best time in string to milliseconds
        String[] timeParts = bestTime.split(":");
        String[] secMilliSec = timeParts[2].split("\\.");

        bestTimeMilliSec = (Long.parseLong(timeParts[0]) * 60 * 60) + // Hours to Seconds
                (Long.parseLong(timeParts[1]) * 60) +  // Minutes to Seconds
                Long.parseLong(secMilliSec[0]); // Seconds
        //Seconds to Milliseconds
        bestTimeMilliSec *= 1000;

        //Not shwoing the milliseconds now
        //bestTimeMilliSec += Long.parseLong(secMilliSec[1]);

        //Check if the previous game is stored and load if required
        if(sharedPreferences.getString("P15_SavedGame", "No").equals("Yes")) {
            //Previous game was saved. Check if the user wants to continue that or wants to start a new game
            AlertDialog.Builder popupMsgBuilder = new AlertDialog.Builder(this);

            popupMsgBuilder.setTitle("Confirm");
            popupMsgBuilder.setMessage("Continue Previous Game?");
            popupMsgBuilder.setCancelable(false);

            popupMsgBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    loadPrevGame = true;
                    startGame();
                }
            });

            popupMsgBuilder.setNegativeButton("New Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    loadPrevGame = false;
                    startGame();
                }
            });

            AlertDialog popupMsg = popupMsgBuilder.create();

            sharedPreferences.edit().putString("P15_SavedGame", "No").apply();
            popupMsg.show();
        }
        else { startGame();}

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

    }

    @Override
    public void onBackPressed() {

        //Handle saving the game, if In Progress
        AlertDialog.Builder popupMsgBuilder = new AlertDialog.Builder(this);

        popupMsgBuilder.setTitle("Confirm");
        popupMsgBuilder.setMessage("Game is in progress!");

        popupMsgBuilder.setPositiveButton("Save & Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Save the Game before closing
                //SharedPreferences to save the gavem
                sharedPreferences.edit().putString("P15_SavedGame", "Yes").apply();

                //Get the Current time lapse
                String currTime = scoreText.getText().toString().split("\n")[1];
                String[] timeParts = currTime.split(":");
                String[] secMilliSec = timeParts[2].split("\\.");

                currScoreTime = (Long.parseLong(timeParts[0]) * 60 * 60) + // Hours to Seconds
                        (Long.parseLong(timeParts[1]) * 60) +  // Minutes to Seconds
                        Long.parseLong(secMilliSec[0]); // Seconds
                //Seconds to Milliseconds
                currScoreTime *= 1000;

                //Store the current gameState
                for(int row=0; row < 4; row++){
                    for(int col=0; col < 4; col++){
                        sharedPreferences.edit().putInt("P15_" + row + "_" + col, gameState[row][col]).apply();
                    }
                }
                sharedPreferences.edit().putInt("P15_PrevMoves", noOfMoves).apply();
                sharedPreferences.edit().putLong("P15_PrevTimeMilliSec", currScoreTime).apply();

                Toast.makeText(Puzzle15.this,"Game Saved.", Toast.LENGTH_SHORT).show();


                //Close the game and return to main menu
                stopwatch.stop();
                handler.removeCallbacks(runnable);

                Intent mainMenuIntent = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(mainMenuIntent);
                finish();
            }
        });

        popupMsgBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Close the game and return to main menu
                stopwatch.stop();
                handler.removeCallbacks(runnable);

                Intent mainMenuIntent = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(mainMenuIntent);
                finish();
            }
        });

        if (gameInProgress){
            //Pause the clock as we are showing the confirmation pop-up msg
            stopwatch.pause();
            handler.removeCallbacks(runnable);

            final AlertDialog popupMsg = popupMsgBuilder.create();

            //To restrict clicking outside of AlertDialog
            popupMsg.setCanceledOnTouchOutside(false);
            popupMsg.setCancelable(false);

            popupMsg.show();

        } else {
            stopwatch.stop();
            handler.removeCallbacks(runnable);

            Intent mainMenuIntent = new Intent(getApplicationContext(), MainMenu.class);
            startActivity(mainMenuIntent);
            finish();
        }



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
