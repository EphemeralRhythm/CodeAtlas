package com.example.codeatlas;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class MapLevelHelper {
    public static void initTopBar(FirebaseFirestore db, Activity activity){
        TextView rankView = activity.findViewById(R.id.rankText);
        TextView starsView = activity.findViewById(R.id.xpText);

        int stars = activity.getIntent().getExtras().getInt("stars");
        int rank = activity.getIntent().getExtras().getInt("rank");

        starsView.setText(String.valueOf(stars));
        rankView.setText(String.valueOf(rank));
    }
    public static void fetchLevelData(FirebaseFirestore db, String track, String topic, int level, int page, Activity activity){
        DocumentReference pageRef = db.collection("tracks")
                .document(track)
                .collection("topics")
                .document(topic)
                .collection("levels")
                .document(String.valueOf(level))
                .collection("pages")
                .document(String.valueOf(page));

        pageRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> mp =  document.getData();
                    Page p = new Page();
                    p.setIndex(page);
                    p.setType(Math.toIntExact((Long) mp.get("type")));
                    p.setContent((String) mp.get("content"));

                    if(p.getType() == Page.QUIZ){
                        String[] optionsList = {"option1", "option2", "option3", "option4"};
                        p.choices = new ArrayList<>();

                        for(String option: optionsList){
                            p.choices.add((String) mp.get(option));
                        }

                        p.setCorrectAnswer((String) mp.get("correctAnswer"));
                    }

                    LevelInterface levelInterface = (LevelInterface) activity;
                    levelInterface.updateUI(p);
                } else {
                }
            } else {
            }
        });
    }

    public static void nextPage(FirebaseFirestore db, String track, String topic,
                                int level, int page, Activity activity, boolean increment, FragmentManager fm){
        DocumentReference pageRef = db.collection("tracks")
                .document(track)
                .collection("topics")
                .document(topic)
                .collection("levels")
                .document(String.valueOf(level))
                .collection("pages")
                .document(String.valueOf(page));

        pageRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    Intent intent;
                    int type = Math.toIntExact((Long) document.getLong("type"));
                    if(type == Page.INFO){
                        intent = new Intent(activity, InfoPageActivity.class);
                    }
                    else{
                        intent = new Intent(activity, QuizActivity.class);
                    }

                    int stars = activity.getIntent().getExtras().getInt("stars");
                    int rank = activity.getIntent().getExtras().getInt("rank");
                    int correctAnswers = activity.getIntent().getExtras().getInt("correct_answers");
                    int totalAnswers = activity.getIntent().getExtras().getInt("total_answers");
                    if(increment)
                        correctAnswers++;

                    intent.putExtra("track", track);
                    intent.putExtra("topic", topic);
                    intent.putExtra("level", level);
                    intent.putExtra("page", page);
                    intent.putExtra("stars", stars);
                    intent.putExtra("rank", rank);
                    intent.putExtra("correct_answers", correctAnswers);
                    intent.putExtra("total_answers", totalAnswers);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    activity.startActivity(intent);
                    return;
                }
                else {
                    int correctAnswers = activity.getIntent().getExtras().getInt("correct_answers");
                    int totalAnswers = activity.getIntent().getExtras().getInt("total_answers");

                    LevelCompletedDialog dialog = new LevelCompletedDialog( correctAnswers, totalAnswers);
                    dialog.show(fm, null);
                }
            }
        });
    }
}
