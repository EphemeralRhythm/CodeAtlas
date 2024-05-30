package com.example.codeatlas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.adservices.topics.Topic;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import kotlin.jvm.internal.CollectionToArray;

public class ArticlesActivity extends BaseActivity {
    ArrayList<ArticleTopic> topics;
    ArrayList<ArticleTopic> immutableTopicsList;
    FirebaseFirestore db;
    SearchView searchView;
    ArticleTopicAdapter adapter;
    ImageView searchIcon;
    TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        setupBackButton();

        db = FirebaseFirestore.getInstance();
        initLayoutComponents();
        initSearchIcon();
        initSearchView();
        fetchTopics();
    }

    public void initLayoutComponents(){
        searchView = findViewById(R.id.searchView);
        searchIcon = findViewById(R.id.articlesSearchIcon);
        header = findViewById(R.id.headerArticles);
    }

    public void switchBackButton(){
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setImageResource(R.drawable.close_icon);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setVisibility(View.GONE);
                filterTopics("");
                header.setVisibility(View.VISIBLE);
                searchIcon.setVisibility(View.VISIBLE);
                backButton.setImageResource(R.drawable.back_icon);
                setupBackButton();
            }
        });
    }

    public void initSearchIcon(){
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchIcon.setVisibility(View.GONE);
                header.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                searchView.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
                }
                switchBackButton();
            }
        });
    }
    public void initSearchView(){
        int colorWhite = getResources().getColor(android.R.color.white);
        int colorHint = getResources().getColor(R.color.nerfed_white);
        float textSize = 18f;

        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinsregular);

        try {
            Field searchAutoCompleteField = SearchView.class.getDeclaredField("mSearchSrcTextView");
            searchAutoCompleteField.setAccessible(true);
            TextView searchAutoComplete = (TextView) searchAutoCompleteField.get(searchView);

            searchAutoComplete.setTextColor(colorWhite);
            searchAutoComplete.setHintTextColor(colorHint);
            searchAutoComplete.setTextSize(textSize);
            searchAutoComplete.setTypeface(typeface);
        }

        catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTopics(newText);
                Log.d("newText", newText + ", " + adapter.getItemCount());
                return true;
            }
        });
    }

    public void filterTopics(String text){
        topics.clear();
        for(ArticleTopic topic: immutableTopicsList){
            if(text == null || text.isEmpty() || topic.getTitle().toLowerCase().contains(text.toLowerCase())){
                topics.add(topic);
                continue;
            }

            ArticleTopic temp = new ArticleTopic();
            temp.setTitle(topic.getTitle());
            temp.setArticles(new ArrayList<>());

            for(Article article: topic.getArticles()){
                if(article.getTitle().toLowerCase().contains(text.toLowerCase())){
                    temp.getArticles().add(article);
                }
            }

            if(!temp.getArticles().isEmpty()){
                topics.add(temp);
            }
        }

        for(ArticleTopic topic: immutableTopicsList){
            Log.d("newText", topic.getTitle());
        }

        adapter.notifyDataSetChanged();
    }

    public void fetchTopics(){
        immutableTopicsList = new ArrayList<>();
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
                                    immutableTopicsList.add(topic);

                                    if (immutableTopicsList.size() == pTask.getResult().size()) {
                                        Collections.sort(immutableTopicsList, new Comparator<ArticleTopic>() {
                                            @Override
                                            public int compare(ArticleTopic t1, ArticleTopic t2) {
                                                return Long.compare(t1.index, t2.index);
                                            }
                                        });

                                        topics = new ArrayList<>();
                                        topics.addAll(immutableTopicsList);

                                        RecyclerView topicsRV = findViewById(R.id.topicsList);
                                        RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(ArticlesActivity.this);
                                        topicsRV.setLayoutManager(layoutManger);
                                        adapter = new ArticleTopicAdapter(topics, ArticlesActivity.this);
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