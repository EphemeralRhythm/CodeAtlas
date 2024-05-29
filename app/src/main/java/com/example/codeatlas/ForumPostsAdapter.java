package com.example.codeatlas;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.noties.markwon.Markwon;

public class ForumPostsAdapter extends RecyclerView.Adapter {

    private ArrayList<ForumPost> posts;
    public View.OnClickListener listener;
    private FirebaseStorage storage;
    Context context;
    public ForumPostsAdapter(ArrayList<ForumPost> posts, Context context, FirebaseStorage storage){
        this.posts = posts;
        this.context = context;
        this.storage = storage;
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView, contentView, authorView;
        public CircleImageView pfp;

        public TitleViewHolder(@NonNull View itemView){
            super(itemView);

            contentView = itemView.findViewById(R.id.content);
            authorView = itemView.findViewById(R.id.authorUsername);
            pfp = itemView.findViewById(R.id.profilePicture);
            itemView.setTag(this);
        }

        public TextView getContentView(){
            return contentView;
        }

        public TextView getAuthorView(){
            return authorView;
        }
        public CircleImageView getPfp(){
            return pfp;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_comment, parent, false);
        return new TitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TitleViewHolder vh = (TitleViewHolder) holder;

        ForumPost post = posts.get(position);

        vh.getAuthorView().setText(post.getAuthorUsername());

        Markwon markwon = Markwon.builder(context).build();
        markwon.setMarkdown(vh.getContentView(), post.getContent());

        User u = new User();
        u.setId(post.getAuthorId());
        PicassoHelper.setProfilePictureInto(vh.getPfp(), u, storage);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
}
