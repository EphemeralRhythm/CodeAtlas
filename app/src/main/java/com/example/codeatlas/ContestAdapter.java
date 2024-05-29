package com.example.codeatlas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.noties.markwon.Markwon;

public class ContestAdapter extends RecyclerView.Adapter {

    private ArrayList<Contest> contests;
    public ContestAdapter(ArrayList<Contest> contests){
        this.contests = contests;
    }

    public class ContestViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, startTextView, durationTextView, dayOfMonthView, dayOfWeekView;

        public ContestViewHolder(@NonNull View itemView){
            super(itemView);
            nameTextView = itemView.findViewById(R.id.detailsText);
            startTextView = itemView.findViewById(R.id.startText);
            durationTextView = itemView.findViewById(R.id.lengthText);
            dayOfMonthView = itemView.findViewById(R.id.dayNbText);
            dayOfWeekView = itemView.findViewById(R.id.dayOfWeekText);

            itemView.setTag(this);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_item, parent, false);
        return new ContestAdapter.ContestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Contest contest = contests.get(position);

        long startTimeMillis = contest.getStartTimeSeconds() * 1000;
        Date startDate = new Date(startTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        String dayOfWeek = new SimpleDateFormat("EEE", Locale.ENGLISH).format(startDate);
        String dayOfMonth = new SimpleDateFormat("dd", Locale.ENGLISH).format(startDate);
        String timeOfDay = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(startDate);

        ContestViewHolder vh = (ContestViewHolder) holder;
        vh.dayOfWeekView.setText(dayOfWeek);
        vh.dayOfMonthView.setText(dayOfMonth);
        vh.startTextView.setText("Start: " + timeOfDay);
        vh.nameTextView.setText(contest.getName());

        long duration = contest.getDurationSeconds() / 60;
        vh.durationTextView.setText(String.format("Length: %02d:%02d", duration / 60  ,  duration % 60));
    }

    @Override
    public int getItemCount() {
        return contests.size();
    }
}
