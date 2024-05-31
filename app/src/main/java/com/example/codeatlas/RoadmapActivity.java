package com.example.codeatlas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RoadmapActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ImageButton eventBtn;
    private String track;
    private String topic;
    private FirebaseFirestore db;
    private ArrayList<Level> levels;
    private int completedLevels;
    private ImageView listTrackImageView, topicImageView;
    private TextView trackTextView, topicTextView, progressView, heartsTextView, diamondsTextView, starsTextView;
    private FirestoreHelper firestoreHelper = new FirestoreHelper();



    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(RoadmapActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("main", MODE_PRIVATE);
        track = prefs.getString("track", "Getting Started");
        topic = prefs.getString("topic", "Introduction to C++");

        mAuth = FirebaseAuth.getInstance();
        Navbar.initNavBar(RoadmapActivity.this);

        initLayoutComponents();
        getUserProgress();
        initUI();
        initLivesWorker();
        initListeners();
    }

    protected void initLayoutComponents(){
        eventBtn = findViewById(R.id.eventBtn);
        listTrackImageView = findViewById(R.id.listTrackImageView);
        topicImageView = findViewById(R.id.rectangleImageChapterName);
        trackTextView = findViewById(R.id.lessonTagText);
        topicTextView = findViewById(R.id.chapterNameText);
        progressView = findViewById(R.id.percentText);

        heartsTextView = findViewById(R.id.livesTextRoadmap);
        diamondsTextView = findViewById(R.id.gemsTextRoadmap);
        starsTextView = findViewById(R.id.xpTextRoadmap);
    }
    public void initUI(){
        trackTextView.setText(track);
        topicTextView.setText(topic);
        updateUserProgress();
        updateUserStats();
    }
    private void initListeners(){

        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoadmapActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });

        listTrackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoadmapActivity.this, SelectTrackActivity.class);
                startActivity(intent);
            }
        });

        topicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoadmapActivity.this, SelectTopicActivity.class);
                startActivity(intent);
            }
        });
    }

    public void fetchTopicData(String trackName, String topicName) {
        levels = new ArrayList<>();
        CollectionReference levelsRef = db.collection("tracks")
                .document(trackName)
                .collection("topics")
                .document(topicName)
                .collection("levels");

        levelsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int totalLevels = task.getResult().size();
                for (QueryDocumentSnapshot levelDoc : task.getResult()) {
                    Level level = new Level();
                    level.setIndex(Integer.parseInt(levelDoc.getId()));
                    if(level.getIndex() - 1 == completedLevels){
                        level.setState(Level.OPEN);
                    }
                    else if(level.getIndex() - 1 > completedLevels){
                        level.setState(Level.LOCKED);
                    }
                    else{
                        level.setState(Level.COMPLETED);
                    }
                    level.pages = new ArrayList<>();
                    levels.add(level);

                    if(levels.size() == totalLevels){
                        updateUI();
                    }
                }
            } else {
            }
        });
    }


    public void updateUI(){
        RecyclerView mapView = findViewById(R.id.levelsView);

        RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(RoadmapActivity.this);
        mapView.setLayoutManager(layoutManger);
        LevelsAdapter adapter = new LevelsAdapter(levels, RoadmapActivity.this);
        adapter.setOnClickListener(listener);
        mapView.setAdapter(adapter);
    }

    public void getUserProgress() {
        String userId = mAuth.getCurrentUser().getUid();
        Log.d("levels", userId);
        DocumentReference userRef = db.collection("tracks")
                .document(track)
                .collection("topics")
                .document(topic)
                .collection("users")
                .document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    completedLevels = Math.toIntExact(document.getLong("level"));
                } else {
                    userRef.set(Collections.singletonMap("level", 0)).addOnCompleteListener(setTask -> {
                    });
                }
                Log.d("levels", "Completed " + completedLevels);
                fetchTopicData(track, topic);
            } else {
            }
        });
    }

    public void initLivesWorker() {
        WorkManager workManager = WorkManager.getInstance(this);
        LiveData<List<WorkInfo>> workInfos = workManager.getWorkInfosForUniqueWorkLiveData("Lives Worker");

        workInfos.observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfoList) {
                boolean workEnqueued = false;
                if (workInfoList != null && !workInfoList.isEmpty()) {
                    for (WorkInfo workInfo : workInfoList) {
                        WorkInfo.State state = workInfo.getState();
                        if (state == WorkInfo.State.ENQUEUED || state == WorkInfo.State.RUNNING) {
                            workEnqueued = true;
                            break;
                        }
                    }
                }

                if (!workEnqueued) {
                    scheduleUpdatePostsWork();
                }
            }
        });
    }

    private void scheduleUpdatePostsWork() {
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(LivesWorker.class, 15, TimeUnit.MINUTES)
                        .setInitialDelay(15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("Lives Worker", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
        observeWorkCompletion(periodicWorkRequest.getId());
    }

    private void observeWorkCompletion(UUID workRequestId) {
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequestId)
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            onWorkCompleted();
                        }
                    }
                });
    }

    private void onWorkCompleted() {
        initUI();
    }

    public void updateUserProgress(){
        firestoreHelper.fetchUserProgressForTopic(mAuth.getCurrentUser().getUid(), track, topic, completedLevels -> {
            firestoreHelper.fetchTotalLevelsForTopic(track, topic, totalLevels -> {
                if (totalLevels > 0) {
                    int progressPercentage = (int) ((completedLevels / (float) totalLevels) * 100);
                    updateProgressUI(progressPercentage);
                } else {
                    updateProgressUI(0);
                }
            });
        });
    }

    public void updateProgressUI(int percentage){
        progressView.setText(percentage + "%");
    }
    public void updateUserStats(){
        String uid = mAuth.getUid();
        DocumentReference ref = db.collection("users").document(uid);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                int hearts = Math.toIntExact(task.getResult().getLong("hearts"));
                int stars = Math.toIntExact(task.getResult().getLong("stars"));
                int diamonds = Math.toIntExact(task.getResult().getLong("diamonds"));

                heartsTextView.setText(String.valueOf(hearts));
                diamondsTextView.setText(String.valueOf(diamonds));
                starsTextView.setText(String.valueOf(stars));
            }
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) view.getTag();
            int position = vh.getAdapterPosition();
            Level level = levels.get(position);

            if(level.getState() == Level.LOCKED){
                return;
            }

            Intent intent = new Intent(RoadmapActivity.this, LessonActivity.class);
            intent.putExtra("level", position + 1);
            intent.putExtra("track", track);
            intent.putExtra("topic", topic);
            startActivity(intent);
        }
    };
}