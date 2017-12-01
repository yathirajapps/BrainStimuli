package com.yathirajjp.brainstimuli;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game2048 extends AppCompatActivity {

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private Handler mAdHandler;
    private Runnable mDisplayAd;

    int[][] gameState, lastGameState, undoGameState, animateTiles;
    int score, undoScore, highScore, noOfMoves, bestMoves;
    TextView scoreText, highScoreText, noOfMovesText, highScoreMsgTextView;
    SharedPreferences sharedPreferences;
    boolean isGameComplete, undoAction;
    GridLayout gridLayout;
    Animation animation;


    //This procedure gets the Array value of given position.  Returns -1 in case of invalid position
    public int getArrayValue(int i, int j) {
        int val;
        try {

            val = gameState[i][j];
            return val;

        } catch (Exception e) {
            return -1;
        }
    }


    //This procedure checks if the game is over
    public boolean isGameOver(){

        // Check the positions {1,1} {1,2} {2,1} and {2,2}
        for (int i=0; i<4; i++){
            for (int j=0; j<4; j++){
                if (gameState[i][j] == 0 ||
                        gameState[i][j] == getArrayValue(i-1, j) ||
                        gameState[i][j] == getArrayValue(i, j+1) ||
                        gameState[i][j] == getArrayValue(i+1, j) ||
                        gameState[i][j] == getArrayValue(i, j-1) ){
                    return false;
                }
            }
        }

        return true;
    }



    //This procedure will generate random number (2 or 4) randomly at the vacant place
    public void generateRandNum(int num){

        boolean emptyTileFound = false;
        outerloop:
        for (int i=0; i<4; i++){
            for (int j=0; j<4; j++){
                if (gameState[i][j] == 0){
                    emptyTileFound = true;
                    break outerloop;
                }
            }
        }

        if (emptyTileFound) {
            int i, j, randNum;
            Random random = new Random();

            i = random.nextInt(4);
            j = random.nextInt(4);

            while (gameState[i][j] != 0) {
                i = random.nextInt(4);
                j = random.nextInt(4);
            }

            randNum = (random.nextInt(num) + 1) * 2;

            gameState[i][j] = randNum;
        }
    }


    //This procedure will load the tiles as per the values in gameState array
    public void loadTiles(){
        int imageResId, drawableResId;
        ImageView imageView;

        for (int i=0; i<4; i++){
            for (int j=0; j<4; j++){
                imageResId = getResources().getIdentifier("image" + Integer.toString(i) + Integer.toString(j), "id", getPackageName());
                drawableResId = getResources().getIdentifier("tile" + Integer.toString(gameState[i][j]), "drawable", getPackageName());

                imageView = (ImageView)findViewById(imageResId);

                imageView.setImageResource(drawableResId);

                if (!undoAction && animateTiles[i][j] == 1){
                    //Animate this tile
                    imageView.startAnimation(animation);
                }

            }
        }

        //Check if the Game is Over, Immovable tiles found
        if (isGameOver()){

            gridLayout.setAlpha(0.5f);
            gridLayout.setEnabled(false);

            // Show the custom dialog
            if (highScoreMsgTextView.getVisibility() == View.VISIBLE) {
                showCustomDialog("Game Over!", "New High Score: " + Integer.toString(score));
            } else {
                showCustomDialog("Game Over!", "Your Score: " + Integer.toString(score));
            }

        }

        //Check if the Game is complete by forming a 2048 tile
        if (isGameComplete){

            gridLayout.setAlpha(0.5f);
            gridLayout.setEnabled(false);

            // Show the custom dialog
            if (highScoreMsgTextView.getVisibility() == View.VISIBLE) {
                showCustomDialog("Congratulations, You Won!", "New High Score: " + Integer.toString(score));
            } else {
                showCustomDialog("Congratulations, You Won!", "Your Score: " + Integer.toString(score));
            }
        }
    } // End of loadTiles



    //This procedure handles the movement of tiles to Right when Swiped right
    public void moveSlidesRight(){

        boolean tileMerged;

        for (int i=0; i<4; i++){
            for (int j=3; j>=0; j--){
                animateTiles[i][j] =0;
                if (gameState[i][j] != 0){
                    tileMerged = false;
                    //Check if there is a tile left to it with same number
                    for (int col=j-1; col>=0; col--){

                        if (getArrayValue(i, col) != 0 && gameState[i][j] != getArrayValue(i, col)){
                            break;
                        }

                        if (gameState[i][j] == getArrayValue(i, col)){
                            //Merge the same numbered cells
                            gameState[i][j] += gameState[i][j];
                            gameState[i][col] = 0;
                            //You need to animate this tile
                            animateTiles[i][j] = 1;
                            tileMerged = true;

                            if (gameState[i][j] == 2048)
                                isGameComplete = true;

                            score += gameState[i][j];
                            break;
                        }
                    }

                    //Check if any empty tiles to the right of it and move this tile to the right most possible
                    for (int col=3; col>j; col--){
                        if (gameState[i][col] == 0){
                            gameState[i][col] = gameState[i][j];
                            gameState[i][j] =0;
                            if (tileMerged) {
                                //You need to animate the tile i,col not the i,j
                                animateTiles[i][col] = 1;
                                animateTiles[i][j] = 0;
                            }

                            if (gameState[i][col] <=4 && (col - j) > 1)
                                score++;

                            break;
                        }
                    }
                }
            }
        }

    } // End of Move Right


    //This procedure handles the movement of tiles to Left when Swiped left
    public void moveSlidesLeft(){

        boolean tileMerged;

        for (int i=0; i<4; i++){
            for (int j=0; j<4; j++){
                animateTiles[i][j] =0;
                if (gameState[i][j] != 0){
                    tileMerged = false;
                    //Check if there is a tile right to it with same number
                    for (int col=j+1; col<4; col++){

                        if (getArrayValue(i, col) != 0 && gameState[i][j] != getArrayValue(i, col)){
                            break;
                        }

                        if (gameState[i][j] == getArrayValue(i, col)){
                            //Merge the same numbered cells
                            gameState[i][j] += gameState[i][j];
                            gameState[i][col] = 0;
                            //You need to animate this tile
                            animateTiles[i][j] = 1;
                            tileMerged = true;

                            if (gameState[i][j] == 2048)
                                isGameComplete = true;

                            score += gameState[i][j];
                            break;
                        }
                    }

                    //Check if any empty tiles to the left of it and move this tile to the left most possible
                    for (int col=0; col<j; col++){
                        if (gameState[i][col] == 0){
                            gameState[i][col] = gameState[i][j];
                            gameState[i][j] =0;
                            if (tileMerged) {
                                //You need to animate the tile i,col not the i,j
                                animateTiles[i][col] = 1;
                                animateTiles[i][j] = 0;
                            }

                            if (gameState[i][col] <=4 && (j - col) > 1)
                                score++;

                            break;
                        }
                    }
                }
            }
        }

    } // End of Move left


    //This procedure handles the movement of tiles to Top when Swiped Up
    public void moveSlidesUp(){

        boolean tileMerged;

        for (int j=0; j<4; j++){
            for (int i=0; i<4; i++){
                animateTiles[i][j] =0;
                if (gameState[i][j] != 0){
                    tileMerged = false;
                    //Check if there is a tile below it with same number
                    for (int row=i+1; row<4; row++){

                        if (getArrayValue(row, j) != 0 && gameState[i][j] != getArrayValue(row, j)){
                            break;
                        }

                        if (gameState[i][j] == getArrayValue(row, j)){
                            //Merge the same numbered cells
                            gameState[i][j] += gameState[i][j];
                            gameState[row][j] = 0;
                            //You need to animate this tile
                            animateTiles[i][j] = 1;
                            tileMerged=true;

                            if (gameState[i][j] == 2048)
                                isGameComplete = true;

                            score += gameState[i][j];
                            break;
                        }
                    }

                    //Check if any empty tiles to the top of it and move this tile to the top most possible
                    for (int row=0; row<i; row++){
                        if (gameState[row][j] == 0){
                            gameState[row][j] = gameState[i][j];
                            gameState[i][j] =0;
                            if (tileMerged) {
                                //You need to animate the tile row,j not the i,j
                                animateTiles[row][j] = 1;
                                animateTiles[i][j] = 0;
                            }

                            if (gameState[row][j] <=4 && (i-row) > 1)
                                score++;

                            break;
                        }
                    }
                }
            }
        }

    } // End of move Up



    //This procedure handles the movement of tiles to Bottom when Swiped down
    public void moveSlidesDown(){

        boolean tileMerged;

        for (int j=0; j<4; j++){
            for (int i=3; i>=0; i--){
                animateTiles[i][j] =0;
                if (gameState[i][j] != 0){
                    tileMerged = false;
                    //Check if there is a tile above it with same number
                    for (int row=i-1; row>=0; row--){

                        if (getArrayValue(row, j) != 0 && gameState[i][j] != getArrayValue(row, j)){
                            break;
                        }

                        if (gameState[i][j] == getArrayValue(row, j)){
                            //Merge the same numbered cells
                            gameState[i][j] += gameState[i][j];
                            gameState[row][j] = 0;
                            tileMerged = true;
                            //You need to animate this tile
                            animateTiles[i][j] = 1;

                            if (gameState[i][j] == 2048)
                                isGameComplete = true;

                            score += gameState[i][j];
                            break;
                        }
                    }

                    //Check if any empty tiles to the bottom of it and move this tile to the bottom most possible
                    for (int row=3; row>i; row--){
                        if (gameState[row][j] == 0){
                            gameState[row][j] = gameState[i][j];
                            gameState[i][j] =0;
                            if (tileMerged) {
                                //You need to animate the tile row,j not the i,j
                                animateTiles[row][j] = 1;
                                animateTiles[i][j] = 0;
                            }

                            if (gameState[row][j] <=4 && (row-i) > 1)
                                score++;

                            break;
                        }
                    }
                }
            }
        }

    } // End of move Down



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

    public void loadAd(){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("7378D97884419E089614BB536911AA73")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void displayInterstitialAd(){


        mAdHandler.postDelayed(mDisplayAd, 1);
    }

    // This procedure will undo the last move
    public void undoGame(View view){
        if (!Arrays.deepEquals(gameState, undoGameState)) {
            undoAction = true;

            //Copy the gameState from undoGameState
            gameState = deepCopy2DArray(undoGameState);

            loadTiles();

            if (highScore == score) {
                highScore = undoScore;
                highScoreText.setText(Integer.toString(highScore));
            }
            score = undoScore;
            scoreText.setText(Integer.toString(score));

            noOfMoves++;
            noOfMovesText.setText(Integer.toString(noOfMoves));

            undoAction = false;
        }

        Log.i("Game2048", "Calling the displayInterstitialAd");
        displayInterstitialAd();

    }


    public void startGame(){

        isGameComplete = false;
        noOfMoves = 0;
        noOfMovesText.setText(Integer.toString(noOfMoves));

        //Initialize the game array
        for (int i=0; i < 4; i++){
            for (int j=0; j < 4; j++){
                gameState[i][j] = 0;
                animateTiles[i][j] = 0;
            }
        }

        //Generate the first Two random numbers
        generateRandNum(2);
        generateRandNum(2);

        //Store the current and initial state as Undo State
        undoGameState = deepCopy2DArray(gameState);

        //Load the tiles as per gameState array
        loadTiles();

        score = 0;
        undoScore = 0;
        scoreText.setText("0");

    }

    public void restartGame(View view){

        gridLayout.setAlpha(1.0f);
        gridLayout.setEnabled(true);

        startGame();
    }


    public void showCustomDialog(final String pHeading, String pScore) {
        final Dialog customDialog = new Dialog(Game2048.this);
        customDialog.setContentView(R.layout.custom_dialog_layout);

        TextView headerTextView = (TextView)customDialog.findViewById(R.id.headerTextView);
        TextView yourScoreTextView = (TextView)customDialog.findViewById(R.id.yourScoreTextView);
        Button menuButton = (Button)customDialog.findViewById(R.id.menuButton);
        Button continueButton = (Button)customDialog.findViewById(R.id.continueButton);
        Button leaderBoardButton = (Button)customDialog.findViewById(R.id.leaderboardButton);

        headerTextView.setText(pHeading);
        yourScoreTextView.setText(pScore);
        if (! pHeading.equals("Game Over!")) {
            continueButton.setText("Continue the game?");
        }
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
                gridLayout.setAlpha(1.0f);
                gridLayout.setEnabled(true);
                //Reset the score
                if (pHeading.equals("Game Over!")) {
                    score = 0;
                    scoreText.setText(Integer.toString(score));
                    highScoreMsgTextView.setVisibility(View.INVISIBLE);
                    startGame();
                }

            }
        });

        //Method for LeadersBoard click
        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Game2048.this, "Sorry, Leadersboard is still under development.", Toast.LENGTH_SHORT).show();
            }
        });

        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.setCancelable(false);
        customDialog.show();

    }

    public void applyHighScore(){
        highScore = score;
        highScoreText.setText(Integer.toString(highScore));
        sharedPreferences.edit().putInt("Game2048HighScore", highScore).apply();
        sharedPreferences.edit().putInt("Game2048BestMoves", noOfMoves).apply();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        //Initialize the mobile ads SDK
        MobileAds.initialize(Game2048.this, "@string/ad_app_id");
        mAdView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  // AdRequest.DEVICE_ID_EMULATOR)    // Test Device: 7378D97884419E089614BB536911AA73
                .build();

        //Start loading the add in the background
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(Game2048.this);
        mInterstitialAd.setAdUnitId("@string/ad_interstitial_unit_id");
        Log.i("AdUnitID", mInterstitialAd.getAdUnitId());
        mInterstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdClosed() {
                loadAd();
            }
        });

        mAdHandler = new Handler(Looper.getMainLooper());
        mDisplayAd = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mInterstitialAd.isLoaded()){
                            mInterstitialAd.show();
                        }
                    }
                });
            }
        };
        loadAd();


        scoreText = (TextView)findViewById(R.id.textViewCurrScoreValue);
        highScoreText = (TextView)findViewById(R.id.textViewHSValue);
        noOfMovesText = (TextView)findViewById(R.id.textViewNoOfMoves);
        gridLayout = (GridLayout)findViewById(R.id.gridBoard4x4);
        highScoreMsgTextView = (TextView)findViewById(R.id.textViewHighScoreMsg);
        sharedPreferences = getSharedPreferences("com.yathirajjp.brainstimuli", MODE_PRIVATE);

        gameState = new int[4][4];
        lastGameState = new int[4][4];
        undoGameState = new int[4][4];
        animateTiles = new int[4][4];

        // Set the animation settings
        animation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f);
        animation.setDuration(100);
        animation.setRepeatCount(0);


        Intent intent = getIntent();
        highScore = intent.getIntExtra("Game2048HighScore", 0);
        highScoreText.setText(Integer.toString(highScore));
        bestMoves = intent.getIntExtra("Game2048BestMoves", 0);

        startGame();

        GridLayout gridBoard = (GridLayout)findViewById(R.id.gridBoard4x4);
        gridBoard.setOnTouchListener(new OnSwipeTouchListener(Game2048.this){

            @Override
            public void onSwipeBottom() {

                //Get the current state of game copied to another array
                lastGameState = deepCopy2DArray(gameState);
                undoScore = score;

                moveSlidesDown();

                if (!Arrays.deepEquals(lastGameState, gameState)) {
                    noOfMoves++;
                    noOfMovesText.setText(Integer.toString(noOfMoves));
                    //Store the current state as Undo state before generating new number
                    undoGameState = deepCopy2DArray(lastGameState);
                    generateRandNum(1);
                    loadTiles();
                    scoreText.setText(Integer.toString(score));
                    if (score > highScore){
                        applyHighScore();
                    }
                }

            }

            @Override
            public void onSwipeTop() {

                lastGameState = deepCopy2DArray(gameState);
                undoScore = score;

                moveSlidesUp();

                if (!Arrays.deepEquals(lastGameState, gameState)) {
                    noOfMoves++;
                    noOfMovesText.setText(Integer.toString(noOfMoves));
                    //Store the current state as Undo state before generating new number
                    undoGameState = deepCopy2DArray(lastGameState);
                    generateRandNum(1);
                    loadTiles();
                    scoreText.setText(Integer.toString(score));
                    if (score > highScore){
                        applyHighScore();
                    }
                }

            }

            @Override
            public void onSwipeLeft() {

                lastGameState = deepCopy2DArray(gameState);
                undoScore = score;

                moveSlidesLeft();

                if (!Arrays.deepEquals(lastGameState, gameState)) {
                    noOfMoves++;
                    noOfMovesText.setText(Integer.toString(noOfMoves));
                    //Store the current state as Undo state before generating new number
                    undoGameState = deepCopy2DArray(lastGameState);
                    generateRandNum(1);
                    loadTiles();
                    scoreText.setText(Integer.toString(score));
                    if (score > highScore){
                        applyHighScore();
                    }
                }
            }

            @Override
            public void onSwipeRight() {

                lastGameState = deepCopy2DArray(gameState);
                undoScore = score;

                moveSlidesRight();

                if (!Arrays.deepEquals(lastGameState, gameState)) {
                    noOfMoves++;
                    noOfMovesText.setText(Integer.toString(noOfMoves));
                    //Store the current state as Undo state before generating new number
                    undoGameState = deepCopy2DArray(lastGameState);
                    generateRandNum(1);
                    loadTiles();
                    scoreText.setText(Integer.toString(score));
                    if (score > highScore){
                        applyHighScore();
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

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


    @Override
    protected void onPause() {
        if (mAdView != null){
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mAdView != null){
            mAdView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
