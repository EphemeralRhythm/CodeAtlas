package com.example.codeatlas;

import android.app.Activity;
import android.widget.ImageButton;

public class ButtonsAlpha {
    static ImageButton roadmap, articles, leaderboard, community, profile;
    public static void resetButtonsAlpha(Activity activity){

        roadmap = activity.findViewById(R.id.roadmapNavIcon);
        articles = activity.findViewById(R.id.articleNavIcon);
        leaderboard = activity.findViewById(R.id.trophyNavIcon);
        community = activity.findViewById(R.id.communityNavIcon);
        profile = activity.findViewById(R.id.profileNavIcon);

            leaderboard.setAlpha(0.5f);
            roadmap.setAlpha(0.5f);
            community.setAlpha(0.5f);
            profile.setAlpha(0.5f);
            articles.setAlpha(0.5f);
    }
}
