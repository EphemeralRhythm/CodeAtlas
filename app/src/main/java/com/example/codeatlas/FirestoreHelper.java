package com.example.codeatlas;

import android.adservices.topics.Topic;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FirestoreHelper {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void fetchTracksWithProgress(String userId, FirestoreCallback callback) {
        CollectionReference tracksRef = db.collection("tracks");
        tracksRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Track> tracks = new ArrayList<>();
                for (QueryDocumentSnapshot trackDoc : task.getResult()) {
                    String trackName = trackDoc.getId();
                    fetchTrackProgress(userId, trackName, (completed, total) -> {
                        tracks.add(new Track(trackName, completed, total));
                        if (tracks.size() == task.getResult().size()) {
                            callback.onCallback(tracks);
                        }
                    });
                }
            } else {
            }
        });
    }

    private void fetchTrackProgress(String userId, String trackName, TrackProgressCallback callback) {
        CollectionReference topicsRef = db.collection("tracks").document(trackName).collection("topics");
        topicsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AtomicInteger totalCompleted = new AtomicInteger();
                AtomicInteger totalLevels = new AtomicInteger();
                AtomicInteger topicsCount = new AtomicInteger(task.getResult().size());
                if (topicsCount.get() == 0) {
                    callback.onCallback(totalCompleted.get(), totalLevels.get());
                    return;
                }
                for (QueryDocumentSnapshot topicDoc : task.getResult()) {
                    String topicName = topicDoc.getId();
                    fetchUserProgress(userId, trackName, topicName, completed -> {
                        totalCompleted.addAndGet(completed);
                        fetchLevelsCount(trackName, topicName, levels -> {
                            totalLevels.addAndGet(levels);
                            topicsCount.getAndDecrement();
                            if (topicsCount.get() == 0) {
                                callback.onCallback(totalCompleted.get(), totalLevels.get());
                            }
                        });
                    });
                }
            } else {
            }
        });
    }

    private void fetchUserProgress(String userId, String trackName, String topicName, UserProgressCallback callback) {
        db.collection("tracks").document(trackName).collection("topics")
                .document(topicName).collection("users").document(userId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        int completedLevels = task.getResult().getLong("level").intValue();
                        callback.onCallback(completedLevels);
                    } else {
                        callback.onCallback(0);
                    }
                });
    }

    private void fetchLevelsCount(String trackName, String topicName, LevelsCountCallback callback) {
        db.collection("tracks").document(trackName).collection("topics")
                .document(topicName).collection("levels")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int levelsCount = task.getResult().size();
                        callback.onCallback(levelsCount);
                    } else {
                    }
                });
    }

    public void fetchUserProgressForTopic(String userId, String trackName, String topicName, UserProgressCallback callback) {
        db.collection("tracks").document(trackName).collection("topics")
                .document(topicName).collection("users").document(userId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        int completedLevels = task.getResult().getLong("level").intValue();
                        callback.onCallback(completedLevels);
                    } else {
                        callback.onCallback(0);
                    }
                });
    }

    public void fetchTotalLevelsForTopic(String trackName, String topicName, LevelsCountCallback callback) {
        db.collection("tracks").document(trackName).collection("topics")
                .document(topicName).collection("levels")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int levelsCount = task.getResult().size();
                        callback.onCallback(levelsCount);
                    } else {
                        callback.onCallback(0);
                    }
                });
    }

    public void fetchTopicsForTrack(String trackName, TopicsCallback callback){
        ArrayList<RoadmapTopic> topics = new ArrayList<>();
        db.collection("tracks").document(trackName).collection("topics")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for(QueryDocumentSnapshot topicSnap: task.getResult()){
                            String topicName = topicSnap.getId();
                            RoadmapTopic topic = new RoadmapTopic(topicName, 1, 1);
                            topics.add(topic);
                        }

                        callback.onCallback(topics);
                    } else {
                        callback.onCallback(topics);
                    }
                });
    }

    public interface FirestoreCallback {
        void onCallback(ArrayList<Track> tracks);
    }
    public interface TopicsCallback {
        void onCallback(ArrayList<RoadmapTopic> topics);
    }

    public interface TrackProgressCallback {
        void onCallback(int completed, int total);
    }

    public interface UserProgressCallback {
        void onCallback(int completed);
    }

    public interface LevelsCountCallback {
        void onCallback(int levels);
    }
}
