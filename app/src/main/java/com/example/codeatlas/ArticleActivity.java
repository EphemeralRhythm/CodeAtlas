package com.example.codeatlas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.noties.markwon.Markwon;

public class ArticleActivity extends BaseActivity {
    Markwon markwon;
    String title, content, topic;

    TextView headerView, contentView, topicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_article);
        setupBackButton();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        headerView = findViewById(R.id.headerArticle);
        contentView = findViewById(R.id.contentView);
        topicView = findViewById(R.id.topicName);

        topic = getIntent().getExtras().getString("topic");
        content = getIntent().getExtras().getString("content");
        title = getIntent().getExtras().getString("title");

        headerView.setText(title);
        topicView.setText(topic);

        markwon = Markwon.builder(this).build();
        markwon.setMarkdown(contentView, content.replace("\\n", "\n"));

    }
}