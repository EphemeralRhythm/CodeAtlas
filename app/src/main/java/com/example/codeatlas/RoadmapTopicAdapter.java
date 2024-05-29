package com.example.codeatlas;

import android.adservices.topics.Topic;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoadmapTopicAdapter extends RecyclerView.Adapter {

    View.OnClickListener listener;
    private ArrayList<RoadmapTopic> topics;

    public RoadmapTopicAdapter(ArrayList<RoadmapTopic> topics){
        this.topics = topics;
    }

    public class RoadmapTopicViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public RoadmapTopicViewHolder(@NonNull View itemView){
            super(itemView);

            titleView = itemView.findViewById(R.id.title);

            itemView.setOnClickListener(listener);
            itemView.setTag(this);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
        return new RoadmapTopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RoadmapTopicViewHolder vh = (RoadmapTopicViewHolder) holder;

        RoadmapTopic topic = topics.get(position);

        vh.titleView.setText(topic.name);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
}
