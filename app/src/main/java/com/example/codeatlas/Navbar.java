package com.example.codeatlas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Navbar {
    public static void initNavBar(Activity activity){
        ImageButton roadmap, articles, leaderboard, community, profile;
        roadmap = activity.findViewById(R.id.roadmapNavIcon);
        articles = activity.findViewById(R.id.articleNavIcon);
        leaderboard = activity.findViewById(R.id.trophyNavIcon);
        community = activity.findViewById(R.id.communityNavIcon);
        profile = activity.findViewById(R.id.profileNavIcon);

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, LeaderboardActivity.class);
                activity.startActivity(intent);
            }
        });
        articles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ArticlesActivity.class);
                activity.startActivity(intent);
            }
        });

        roadmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, RoadmapActivity.class);
                activity.startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, Profile.class);
                activity.startActivity(intent);
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ForumsActivity.class);
                activity.startActivity(intent);
            }
        });
    }
}
