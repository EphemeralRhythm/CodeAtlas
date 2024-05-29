package com.example.codeatlas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.MonthDay;
import java.util.Map;

public class DialogLosingLives extends DialogFragment {
    long lives;
    TextView timeView;
    LinearLayout livesLayout;
    SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_losing_lives, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timeView = view.findViewById(R.id.timeText);
        livesLayout = view.findViewById(R.id.hearts_layout);

        for(int i = 0; i < 5; i++){
            ImageView img = (ImageView) livesLayout.getChildAt(i);
            img.setImageResource(R.drawable.heart_losed);
        }

        prefs = getActivity().getSharedPreferences("hearts", Context.MODE_PRIVATE);

        updateLives();

        startCountdownTimer();
        return view;
    }

    private void startCountdownTimer() {
        long nextUpdateTime = prefs.getLong(LivesWorker.TIME_UNTIL_LIVES_UPDATE, 0);
        long currentTime = System.currentTimeMillis();

        if(nextUpdateTime == 0){
            nextUpdateTime = currentTime + 15 * 1000 * 60;
        }
        long timeUntilNextUpdate = nextUpdateTime - currentTime;

        if (timeUntilNextUpdate > 0) {
            new CountDownTimer(timeUntilNextUpdate, 1000) {
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    seconds = seconds % 60;

                    timeView.setText("00:" + minutes + ":" + seconds);
                }

                public void onFinish() {
                    updateLives();
                }
            }.start();
        } else {
            updateLives();
        }
    }

    public void updateLives(){
        lives = prefs.getLong("hearts", 0);
        if(lives != 5){
            startCountdownTimer();
        }
        for(int i = 0; i < 5 - lives; i++){
            ImageView img = (ImageView) livesLayout.getChildAt(i);
            img.setImageResource(R.drawable.heart_losed);
        }
    }
}