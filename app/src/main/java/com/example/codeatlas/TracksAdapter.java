package com.example.codeatlas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class TracksAdapter extends RecyclerView.Adapter {

    View.OnClickListener listener;
    private ArrayList<Track> tracks;

    public TracksAdapter(ArrayList<Track> tracks){
        this.tracks = tracks;
    }

    public class TracksViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView, completionView;
        public ProgressBar progressBar;
        public TracksViewHolder(@NonNull View itemView){
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
            titleView = itemView.findViewById(R.id.title);
            completionView = itemView.findViewById(R.id.progressText);

            itemView.setOnClickListener(listener);
            itemView.setTag(this);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        return new TracksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TracksViewHolder vh = (TracksViewHolder) holder;

        Track track = tracks.get(position);
        float progress = 100 * (float) track.completed / track.total;

        vh.titleView.setText(track.name);
        vh.progressBar.setProgress((int) progress, true);
        vh.completionView.setText(track.completed + "/" + track.total);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
}
