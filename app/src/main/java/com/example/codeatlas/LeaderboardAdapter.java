package com.example.codeatlas;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.noties.markwon.Markwon;

public class LeaderboardAdapter extends RecyclerView.Adapter {

    private ArrayList<User> users;
    FirebaseStorage storage;
    Context context;
    public LeaderboardAdapter(ArrayList<User> users, FirebaseStorage storage){
        this.users = users;
        this.storage = storage;
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        public TextView rankView, starsView, userName;
        public CircleImageView pfpView;

        public LeaderboardViewHolder(@NonNull View itemView){
            super(itemView);

            rankView = itemView.findViewById(R.id.rank);
            starsView = itemView.findViewById(R.id.pointsForRanks);
            pfpView = itemView.findViewById(R.id.profilePicture);
            userName = itemView.findViewById(R.id.nameText);

            itemView.setTag(this);
        }

        public TextView getRankView(){
            return rankView;
        }
        public TextView getStarsView(){
            return starsView;
        }
        public CircleImageView getPfpView(){
            return pfpView;
        }
        public TextView getUserName() {
            return userName;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LeaderboardViewHolder vh = (LeaderboardViewHolder) holder;

        User user = users.get(position);

        vh.getRankView().setText(String.valueOf(position + 4));
        vh.getStarsView().setText(String.valueOf(user.getStars()));
        vh.getUserName().setText(user.getUsername());

        StorageReference storageReference = storage.getReference().child("profileImages/" + user.getId() +".jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.person)
                        .error(R.drawable.person)
                        .into(vh.getPfpView());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
