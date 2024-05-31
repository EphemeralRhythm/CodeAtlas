package com.example.codeatlas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EventsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        setupBackButton();
        fetchContests();
    }

    public void fetchContests(){
        CodeforcesApiHelper.fetchUpcomingContests(new CodeforcesApiHelper.ApiCallback() {
            @Override
            public void onSuccess(ArrayList<Contest> contests) {
                for (Contest contest : contests) {
                    Log.d("Codeforces", "Contest: " + contest.getName() + ", Starts at: " + contest.getStartTimeSeconds());
                }

                contests.sort(new Comparator<Contest>() {
                    @Override
                    public int compare(Contest c1, Contest c2) {
                        return Long.compare(c1.getStartTimeSeconds(), c2.getStartTimeSeconds());
                    }
                });

                RecyclerView contestRecyclerView = findViewById(R.id.eventsRecyclerView);
                RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(EventsActivity.this);
                contestRecyclerView.setLayoutManager(layoutManger);
                ContestAdapter adapter = new ContestAdapter(contests);
                contestRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Codeforces", "Error fetching contests", e);
            }
        });
    }
}