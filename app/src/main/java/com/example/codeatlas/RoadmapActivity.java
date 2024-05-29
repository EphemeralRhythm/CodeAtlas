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
import java.util.concurrent.TimeUnit;

public class RoadmapActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ImageButton eventBtn;
    private String track;
    private String topic;
    private FirebaseFirestore db;
    private ArrayList<Level> levels;
    private int completedLevels;
    private ImageView trackImageView, topicImageView;
    private TextView trackTextView, topicTextView, progressView;
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
        trackImageView = findViewById(R.id.rectangleImageLessonTag);
        topicImageView = findViewById(R.id.rectangleImageChapterName);
        trackTextView = findViewById(R.id.lessonTagText);
        topicTextView = findViewById(R.id.chapterNameText);
        progressView = findViewById(R.id.percentText);
    }
    public void initUI(){
        trackTextView.setText(track);
        topicTextView.setText(topic);
        updateUserProgress();
    }
    private void initListeners(){
        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoadmapActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });

        trackImageView.setOnClickListener(new View.OnClickListener() {
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

    private void fetchPagesForLevel(DocumentReference levelRef, Level level, ArrayList<Level> levels, int totalLevels) {
        levelRef.collection("pages").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot pageDoc : task.getResult()) {
                    int pageNumber = Integer.parseInt(pageDoc.getId());

                    Map<String, Object> mp =  pageDoc.getData();
                    Page page = new Page();
                    page.setIndex(pageNumber);
                    page.setType(Math.toIntExact((Long) mp.get("type")));
                    page.setContent((String) mp.get("content"));

                    level.pages.add(page);
                }
                levels.add(level);

                Log.d("levels", "Added a new level");
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

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) view.getTag();
            int position = vh.getAdapterPosition();
            Level level = levels.get(position);

            if(level.getState() == Level.LOCKED){
                return;
            }

            DocumentReference pageRef = db.collection("tracks")
                    .document(track)
                    .collection("topics")
                    .document(topic)
                    .collection("levels")
                    .document(String.valueOf(position + 1))
                    .collection("pages")
                    .document("1");

            pageRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if(document.exists()){
                        Intent intent;
                        int type = Math.toIntExact((Long) document.getLong("type"));
                        if(type == Page.INFO){
                            intent = new Intent(RoadmapActivity.this, InfoPageActivity.class);
                        }
                        else{
                            intent = new Intent(RoadmapActivity.this, QuizActivity.class);
                        }


                        ArrayList<User> users = new ArrayList<>();
                        db.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Map<String, Object> mp = document.getData();
                                                User user = new User();
                                                user.setId(document.getId());
                                                user.setUsername((String) mp.get("username"));
                                                user.setStars((Long) mp.get("stars"));
                                                user.setHearts((Long) mp.get("hearts"));

                                                users.add(user);
                                            }

                                            Collections.sort(users, new Comparator<User>() {
                                                @Override
                                                public int compare(User u1, User u2) {
                                                    return Long.compare(u2.getStars(), u1.getStars());
                                                }
                                            });

                                            mAuth = FirebaseAuth.getInstance();
                                            String email = mAuth.getCurrentUser().getUid();
                                            int rank = -1;
                                            int stars = -1;
                                            int hearts = -1;

                                            for(int i = 0; i < users.size(); i++){
                                                if(users.get(i).getId().equals(email)){
                                                    rank = i + 1;
                                                    stars = Math.toIntExact(users.get(i).getStars());
                                                    hearts = Math.toIntExact(users.get(i).getHearts());

                                                    if(hearts == 0){
                                                        DialogLosingLives dialog = new DialogLosingLives();
                                                        FragmentManager fm = getSupportFragmentManager();
                                                        dialog.show(fm, "Ran Out of Lives");
                                                        return;
                                                    }
                                                    break;
                                                }
                                            }

                                            int total = 0;
                                            if(type == Page.QUIZ)
                                                total = 1;

                                            intent.putExtra("track", track);
                                            intent.putExtra("topic", topic);
                                            intent.putExtra("level", position + 1);
                                            intent.putExtra("page", 1);
                                            intent.putExtra("rank", rank);
                                            intent.putExtra("stars", stars);
                                            intent.putExtra("correct_answers", 0);
                                            intent.putExtra("total_answers", 1);

                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                }
            });
        }
    };
}