package com.example.codeatlas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.noties.markwon.Markwon;

public class LevelsAdapter extends RecyclerView.Adapter {

    private ArrayList<Level> levels;
    public View.OnClickListener listener;
    Context context;
    public LevelsAdapter(ArrayList<Level> levels, Context context){
        this.levels = levels;
        this.context = context;
    }

    public class LevelViewHolder extends RecyclerView.ViewHolder {
        public ImageView platform, icon, ring;

        public LevelViewHolder(@NonNull View itemView){
            super(itemView);

            platform = itemView.findViewById(R.id.map_platform);
            icon = itemView.findViewById(R.id.icon);
            ring = itemView.findViewById(R.id.ring);

            itemView.setOnClickListener(listener);
            itemView.setTag(this);
        }

        public ImageView getPlatform(){
            return platform;
        }
        public ImageView getIcon(){
            return icon;
        }

        public ImageView getRing() {
            return ring;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_level, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LevelViewHolder vh = (LevelViewHolder) holder;

        Level level = levels.get(position);

        if(level.getState() == Level.LOCKED){
            vh.getPlatform().setImageResource(R.drawable.map_icon_disabled);
            vh.getIcon().setImageResource(R.drawable.map_lock);
        }

        else if(level.getState() == Level.OPEN){
            vh.getPlatform().setImageResource(R.drawable.map_icon_enabled);
            vh.getIcon().setImageResource(R.drawable.star);
            vh.getRing().setVisibility(View.VISIBLE);
        }

        else if(level.getState() == Level.COMPLETED){
            vh.getPlatform().setImageResource(R.drawable.map_icon_enabled);
            vh.getIcon().setImageResource(R.drawable.check_circle);
        }

        float bias = ((float) Math.sin(position * Math.PI / 6) + 1) / 2;

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) vh.getRing().getLayoutParams();
        params.horizontalBias = bias;
        vh.getRing().setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
}
