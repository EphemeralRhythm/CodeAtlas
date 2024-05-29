package com.example.codeatlas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectTopicActivity extends AppCompatActivity {
    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) view.getTag();
            int position = vh.getAdapterPosition();
            RoadmapTopic topic = topics.get(position);

            prefs.edit().putString("topic", topic.name);
            Intent intent = new Intent(SelectTopicActivity.this, RoadmapActivity.class);
            startActivity(intent);
        }
    };

    private ArrayList<RoadmapTopic> topics;
    private SharedPreferences prefs;
    private FirestoreHelper firestoreHelper = new FirestoreHelper();
    private ImageButton closeButton;
    private String track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_topic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("main", MODE_PRIVATE);
        track = prefs.getString("track", "Getting Started");

        initLayoutComponents();
        firestoreHelper.fetchTopicsForTrack("track", SelectTopicActivity.this::displayTopics);
    }

    private void initLayoutComponents(){
        closeButton = findViewById(R.id.cancelButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void displayTopics(ArrayList<RoadmapTopic> topics){
        this.topics = topics;

        RecyclerView rv = findViewById(R.id.topicsRecyclerView);
        RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(SelectTopicActivity.this);
        rv.setLayoutManager(layoutManger);
        RoadmapTopicAdapter adapter = new RoadmapTopicAdapter(topics);
        adapter.setOnClickListener(listener);
        rv.setAdapter(adapter);
    }
}