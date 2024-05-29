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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SelectTrackActivity extends AppCompatActivity {
    private FirestoreHelper firestoreHelper = new FirestoreHelper();
    private ImageButton closeButton;
    private ArrayList<Track> tracks;
    SharedPreferences prefs;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) view.getTag();
            int position = vh.getAdapterPosition();

            Track track = tracks.get(position);
            prefs.edit().putString("track", track.name).apply();

            firestoreHelper.fetchTopicsForTrack(track.name, SelectTrackActivity.this::moveToMain);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_track);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initLayoutComponents();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        firestoreHelper.fetchTracksWithProgress(mAuth.getCurrentUser().getUid(), this::displayTracks);
        prefs = getSharedPreferences("main", MODE_PRIVATE);
    }

    private void displayTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
        RecyclerView rv = findViewById(R.id.tracksRecyclerView);
        RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(SelectTrackActivity.this);
        rv.setLayoutManager(layoutManger);
        TracksAdapter adapter = new TracksAdapter(tracks);
        adapter.setOnClickListener(listener);
        rv.setAdapter(adapter);
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

    public void moveToMain(ArrayList<RoadmapTopic> topics){
        prefs.edit().putString("topic", topics.get(0).name).apply();
        Intent intent = new Intent(SelectTrackActivity.this, RoadmapActivity.class);

        startActivity(intent);
    }
}