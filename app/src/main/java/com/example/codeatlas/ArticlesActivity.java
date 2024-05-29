package com.example.codeatlas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.adservices.topics.Topic;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import kotlin.jvm.internal.CollectionToArray;

public class ArticlesActivity extends BaseActivity {
    ArrayList<ArticleTopic> topics;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        setupBackButton();

        db = FirebaseFirestore.getInstance();
        fetchTopics();
    }

    public void fetchTopics(){
        topics = new ArrayList<>();
        CollectionReference collection = db.collection("articles");

        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> pTask) {
                if(pTask.isSuccessful()){
                    for(DocumentSnapshot topicSnapshot: pTask.getResult()){
                        String topicTitle = topicSnapshot.getId();
                        ArticleTopic topic = new ArticleTopic();
                        topic.setTitle(topicTitle);
                        topic.index = Math.toIntExact(topicSnapshot.getLong("index"));

                        CollectionReference articlesCollection = topicSnapshot.getReference().collection("articles");
                        articlesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Article> articles = new ArrayList<>();
                                    for (DocumentSnapshot articleSnapshot : task.getResult()) {
                                        Map<String, Object> mp =  articleSnapshot.getData();
                                        Article article = new Article();
                                        article.setContent((String) mp.get("content"));
                                        article.setTitle(articleSnapshot.getId());
                                        articles.add(article);
                                    }
                                    topic.setArticles(articles);
                                    topics.add(topic);

                                    if (topics.size() == pTask.getResult().size() - 1) {
                                        Collections.sort(topics, new Comparator<ArticleTopic>() {
                                            @Override
                                            public int compare(ArticleTopic t1, ArticleTopic t2) {
                                                return Long.compare(t1.index, t2.index);
                                            }
                                        });

                                        RecyclerView topicsRV = findViewById(R.id.topicsList);
                                        RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(ArticlesActivity.this);
                                        topicsRV.setLayoutManager(layoutManger);
                                        ArticleTopicAdapter adapter = new ArticleTopicAdapter(topics, ArticlesActivity.this);
                                        topicsRV.setAdapter(adapter);
                                    }
                                } else {
                                    Toast.makeText(ArticlesActivity.this, "Mission Failed we'll get them next time", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}