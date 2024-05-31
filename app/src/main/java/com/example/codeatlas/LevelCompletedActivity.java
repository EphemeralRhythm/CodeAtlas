package com.example.codeatlas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class LevelCompletedActivity extends AppCompatActivity {

    TextView correctView, wrongView, diamondsView, starsView;
    ImageButton button;
    LottieAnimationView lottieAnimationView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level_completed);

        lottieAnimationView = findViewById(R.id.perfect_animation_icon);
        lottieAnimationView.setAnimation(R.raw.perfect_animated_icon);
        lottieAnimationView.playAnimation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initLayoutComponents();

        int correct = getIntent().getExtras().getInt("correct");
        int total = getIntent().getExtras().getInt("total");

        int wrong = total - correct;

        initLayoutComponents();
        correctView.setText(String.valueOf(correct));
        wrongView.setText(String.valueOf(wrong));

        diamondsView.setText(String.valueOf(10 * correct));
        starsView.setText(String.valueOf(5 * correct));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelCompletedActivity.this, RoadmapActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initLayoutComponents(){
        correctView = findViewById(R.id.correctAnswers);
        wrongView = findViewById(R.id.wrongAnswers);
        diamondsView = findViewById(R.id.diamonds_amount);
        starsView = findViewById(R.id.stars_amount);
        button = findViewById(R.id.continueButton);
    }
}