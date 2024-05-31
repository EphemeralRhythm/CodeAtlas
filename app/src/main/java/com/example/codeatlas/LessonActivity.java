package com.example.codeatlas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class LessonActivity extends AppCompatActivity {
    String track, topic;

    ViewPager2 viewPager;
    FirebaseFirestore db;
    Level level = new Level();
    LessonAdapter adapter;
    public TextView heartTextView, rankTextView, starsTextView;
    public int correctAnswers, totalAnswers;
    FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lesson);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        level.setIndex(getIntent().getExtras().getInt("level"));
        track = getIntent().getExtras().getString("track");
        topic = getIntent().getExtras().getString("topic");

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firestoreHelper = new FirestoreHelper();

        initLayoutComponents();
        initLevelInfo();
        firestoreHelper.fetchUserInfo(mAuth.getCurrentUser().getUid(), this::updateUserInfo);
        firestoreHelper.fetchUserRank(mAuth.getCurrentUser().getUid(), this::updateUserRank);
    }

    public void initLayoutComponents(){
        viewPager = findViewById(R.id.viewPager);

        heartTextView = findViewById(R.id.livesText);
        viewPager.setTag(this);

        rankTextView = findViewById(R.id.rankText);
        starsTextView = findViewById(R.id.xpText);
    }

    public void updateUserInfo(User user){
        if(user.getHearts() == 0){
            DialogLosingLives dialog = new DialogLosingLives();
            FragmentManager fm = getSupportFragmentManager();

            dialog.show(fm, "Ran out of lives");
        }
        heartTextView.setText(String.valueOf(user.getHearts()));
        starsTextView.setText(String.valueOf(user.getStars()));
    }
    public void updateUserRank(int rank){
        rankTextView.setText(String.valueOf(rank));
    }

    private void initLevelInfo(){
        String trackName = getIntent().getExtras().getString("track", "Getting Started");
        String topicName = getIntent().getExtras().getString("topic", "Introduction to C++");
        int levelIndex = getIntent().getExtras().getInt("level", 0);
        level.pages = new ArrayList<>();

        db.collection("tracks")
                .document(trackName)
                .collection("topics")
                .document(topicName)
                .collection("levels")
                .document(String.valueOf(levelIndex))
                .collection("pages")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot pageDoc : task.getResult()) {
                    int pageNumber = Integer.parseInt(pageDoc.getId());

                    Map<String, Object> mp = pageDoc.getData();
                    Page page = new Page();
                    page.setIndex(pageNumber);
                    page.setType(Math.toIntExact((Long) mp.get("type")));
                    page.setContent((String) mp.get("content"));

                    if (page.getType() == Page.QUIZ) {
                        String[] optionsList = {"option1", "option2", "option3", "option4"};
                        page.choices = new ArrayList<>();

                        for (String option : optionsList) {
                            page.choices.add((String) mp.get(option));
                        }

                        page.setCorrectAnswer((String) mp.get("correctAnswer"));
                    }

                    level.pages.add(page);
                }

                Collections.sort(level.pages, new Comparator<Page>() {
                    @Override
                    public int compare(Page p1, Page p2) {
                        return Integer.compare(p1.getIndex(), p2.getIndex());
                    }
                });

                adapter = new LessonAdapter(this);
                adapter.fragmentManager = getSupportFragmentManager();
                adapter.viewPager = viewPager;
                adapter.context = this;
                adapter.setAllPages(level.pages);

                ArrayList<Page> curPages = new ArrayList<>();
                curPages.add(level.pages.get(0));
                adapter.setPages(curPages);

                viewPager.setAdapter(adapter);

            }
        });
    }

    public void winCallback(int level){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();


        DocumentReference ref = db.collection("tracks").document(track).collection("topics")
                .document(topic).collection("users").document(uid);

                Log.d("updatingStuff", this.level.getIndex() + ", " + level);
                if(this.level.getIndex() > level){
                   ref.get().addOnCompleteListener(task -> {
                       Map<String, Object> mp = task.getResult().getData();
                       mp.put("level", this.level.getIndex());

                       Log.d("updatingStuff", "went here");
                       ref.update(mp);
                   });
                }

        DocumentReference userRef = db.collection("users").document(uid);


        userRef.get().addOnCompleteListener(task -> {
            Map<String, Object> mp = task.getResult().getData();
            mp.put("stars", (long) mp.get("stars") + 5 * correctAnswers);
            mp.put("diamonds", (long) mp.get("stars") + 10 * correctAnswers);
            userRef.update(mp);
        });


        Intent intent = new Intent(LessonActivity.this, LevelCompletedActivity.class);
        intent.putExtra("correct", correctAnswers);
        intent.putExtra("total", totalAnswers);
        startActivity(intent);
    }
    public void winLevel(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        firestoreHelper.fetchUserProgressForTopic(uid, track, topic, this::winCallback);
    }
}