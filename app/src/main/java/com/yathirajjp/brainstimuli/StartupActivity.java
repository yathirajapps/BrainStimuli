package com.yathirajjp.brainstimuli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class StartupActivity extends AppCompatActivity {

    public void showMainMenu(View view){

        Intent mainMenuIntent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(mainMenuIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        ImageView logoImage = (ImageView) findViewById(R.id.logoImage);

        logoImage.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(1250);

    }
}
