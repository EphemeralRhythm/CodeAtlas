package com.example.codeatlas;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LivesWorker extends Worker {
    public static final String TIME_UNTIL_LIVES_UPDATE = "time_until_lives_update";
    public static final long UPDATE_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(15);
    public LivesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        updatePosts();
        return Result.success();
    }

    private void updatePosts() {
        long nextUpdateTime = System.currentTimeMillis() + UPDATE_INTERVAL_MILLIS;
        SharedPreferences prefs  = getApplicationContext().getSharedPreferences("hearts", Context.MODE_PRIVATE);
        prefs.edit().putLong(TIME_UNTIL_LIVES_UPDATE, nextUpdateTime).apply();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("users").document(mAuth.getCurrentUser().getUid());
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> mp = task.getResult().getData();
                Long lives = (Long) mp.get("hearts");
                prefs.edit().putLong("hearts", lives).apply();

                if(lives < 5)
                    mp.put("hearts", lives + 1);

                if(lives == 4){
                    // send notification
                }

                ref.update(mp);
            }
        });
    }
}
