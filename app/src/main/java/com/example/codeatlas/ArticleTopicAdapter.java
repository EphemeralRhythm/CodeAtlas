package com.example.codeatlas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArticleTopicAdapter extends RecyclerView.Adapter {
    private ArrayList<ArticleTopic> topics;
    public View.OnClickListener listener;
    Context context;
    public ArticleTopicAdapter(ArrayList<ArticleTopic> topics, Context context){
        this.context = context;
        this.topics = topics;
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public RecyclerView rv;
        public TopicViewHolder(@NonNull View itemView){
            super(itemView);
            rv = itemView.findViewById(R.id.articlesList);
            rv.setVisibility(View.GONE);
            titleView = itemView.findViewById(R.id.topicName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rv.getVisibility() == View.GONE) {
                        rv.setVisibility(View.VISIBLE);
                        Drawable icon = ContextCompat.getDrawable(context, R.drawable.arrow_up);
                        titleView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null );
                    }
                    else {
                        rv.setVisibility(View.GONE);
                        Drawable icon = ContextCompat.getDrawable(context, R.drawable.arrow_down);
                        titleView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null );
                    }
                }
            });

            itemView.setTag(this);
        }

        public TextView getTitleView(){
            return titleView;
        }
        public RecyclerView getRV(){
            return rv;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ArticleTopicAdapter.TopicViewHolder vh = (ArticleTopicAdapter.TopicViewHolder) holder;

        ArticleTopic topic = topics.get(position);
        ArrayList<Article> articles = topic.getArticles();
        View.OnClickListener childListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) view.getTag();
                int position = vh.getAdapterPosition();
                Article article = articles.get(position);

                Intent intent = new Intent(context, ArticleActivity.class);
                intent.putExtra("topic", topic.getTitle());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("content", article.getContent());

                context.startActivity(intent);
            }
        };

        vh.getTitleView().setText(topic.getTitle());
        RecyclerView rv = vh.getRV();
        RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(context);
        rv.setLayoutManager(layoutManger);
        ArticleListAdapter adapter = new ArticleListAdapter(topic.getArticles());
        adapter.setOnClickListener(childListener);
        rv.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }
}
