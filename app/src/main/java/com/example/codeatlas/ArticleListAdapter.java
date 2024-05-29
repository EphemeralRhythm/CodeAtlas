package com.example.codeatlas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArticleListAdapter extends RecyclerView.Adapter {
    private ArrayList<Article> articles;
    public View.OnClickListener listener;
    public ArticleListAdapter(ArrayList<Article> articles){
        this.articles = articles;
        Log.d("size", " " + articles.size());
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public ArticleViewHolder(@NonNull View itemView){
            super(itemView);

            titleView = itemView.findViewById(R.id.articleTitle);
            itemView.setTag(this);
            itemView.setOnClickListener(listener);
        }

        public TextView getTitleView(){
            return titleView;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_item, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ArticleListAdapter.ArticleViewHolder vh = (ArticleListAdapter.ArticleViewHolder) holder;

        Article article = articles.get(position);
        vh.getTitleView().setText(article.getTitle());
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
}
