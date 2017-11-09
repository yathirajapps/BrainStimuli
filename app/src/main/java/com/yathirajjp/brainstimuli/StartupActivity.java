package com.yathirajjp.brainstimuli;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class StartupActivity extends AppCompatActivity {

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
