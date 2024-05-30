package com.example.codeatlas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        // Duration of the splash screen display (e.g., 3 seconds)
        int splashScreenDuration = 4100;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(intent);
                // Finish the splash activity
                finish();
            }
        }, splashScreenDuration);
    }
}
