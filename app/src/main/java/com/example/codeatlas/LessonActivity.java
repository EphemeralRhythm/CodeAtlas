package com.example.codeatlas;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class LessonActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    FirebaseFirestore db;
    Level level = new Level();
    LessonAdapter adapter;
    TextView heartTextView, rankTextView, starsTextView;
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

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirestoreHelper firestoreHelper = new FirestoreHelper();

        initLayoutComponents();
        initLevelInfo();
        firestoreHelper.fetchUserInfo(mAuth.getCurrentUser().getUid(), this::updateUserInfo);
        firestoreHelper.fetchUserRank(mAuth.getCurrentUser().getUid(), this::updateUserRank);
    }

    public void initLayoutComponents(){
        viewPager = findViewById(R.id.viewPager);

        heartTextView = findViewById(R.id.livesText);
        rankTextView = findViewById(R.id.rankText);
        starsTextView = findViewById(R.id.xpText);
    }

    public void updateUserInfo(User user){
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

                    Map<String, Object> mp =  pageDoc.getData();
                    Page page = new Page();
                    page.setIndex(pageNumber);
                    page.setType(Math.toIntExact((Long) mp.get("type")));
                    page.setContent((String) mp.get("content"));

                    if(page.getType() == Page.QUIZ){
                        String[] optionsList = {"option1", "option2", "option3", "option4"};
                        page.choices = new ArrayList<>();

                        for(String option: optionsList){
                            page.choices.add((String) mp.get(option));
                        }

                        page.setCorrectAnswer((String) mp.get("correctAnswer"));
                    }

                    level.pages.add(page);

                    adapter = new LessonAdapter(this);
                    adapter.viewPager = viewPager;
                    adapter.context = this;
                    adapter.setAllPages(level.pages);

                    ArrayList<Page> curPages = new ArrayList<>();
                    curPages.add(level.pages.get(0));
                    adapter.setPages(curPages);

                    viewPager.setAdapter(adapter);
                }
            }
        });
    }
}