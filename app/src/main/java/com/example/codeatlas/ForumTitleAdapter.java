package com.example.codeatlas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumTitleAdapter extends RecyclerView.Adapter {

    private ArrayList<ForumTitle> forumTitles;
    public View.OnClickListener listener;
    public ForumTitleAdapter(ArrayList<ForumTitle> forumTitles){
        this.forumTitles = forumTitles;
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView, descriptionView, letterView;
        public ImageView fillView;
        public ImageButton button;

        public TitleViewHolder(@NonNull View itemView){
            super(itemView);

            titleView = itemView.findViewById(R.id.title);
            descriptionView = itemView.findViewById(R.id.description);
            letterView = itemView.findViewById(R.id.letter);
            fillView = itemView.findViewById(R.id.fillColor);
            button = itemView.findViewById(R.id.container);
            button.setOnClickListener(listener);
            button.setTag(this);
            itemView.setTag(this);
        }

        public TextView titleView(){
            return titleView;
        }

        public TextView descriptionView(){
            return descriptionView;
        }

        public TextView letterView(){
            return letterView;
        }
        public ImageView fillView(){
            return fillView;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_category, parent, false);
        return new TitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TitleViewHolder vh = (TitleViewHolder) holder;

        ForumTitle title = forumTitles.get(position);
        vh.titleView().setText(title.getTitle());
        vh.letterView().setText(title.getId());
        vh.descriptionView().setText(title.getDescription());
        vh.fillView().setColorFilter(Color.parseColor(title.getColor()));
    }

    @Override
    public int getItemCount() {
        return forumTitles.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
}
