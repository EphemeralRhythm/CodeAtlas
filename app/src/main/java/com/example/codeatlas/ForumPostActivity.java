package com.example.codeatlas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import io.noties.markwon.Markwon;

public class ForumPostActivity extends AppCompatActivity {

    TextView postTitle, postContent, postAuthor, threadCategory, postDate;
    ArrayList<ForumPost> posts;
    Markwon markwon;
    FirebaseFirestore db;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post);

        id = getIntent().getExtras().getString("id", "An");

        db = FirebaseFirestore.getInstance();

        markwon = Markwon.builder(this).build();
        initComponents();
        fetchPost(id);
        fetchPosts(id);
        Navbar.initNavBar(this);
    }

    private void initComponents(){
        postTitle = findViewById(R.id.postTitle);
        postAuthor = findViewById(R.id.authorUsername);
        threadCategory = findViewById(R.id.categoryName);
        postContent = findViewById(R.id.postContent);
        postDate = findViewById(R.id.postDate);
    }
    private void fetchPost(String id){
        db.collection("forumPosts")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> mp =  document.getData();
                            ForumPost p = new ForumPost();
                            p.setId(document.getId());
                            p.setTitle((String) mp.get("title"));
                            p.setContent(((String) mp.get("content")).replace("\\n", "\n"));
                            p.setAuthorId((String) mp.get("authorId"));
                            p.setAuthorUsername((String) mp.get("authorUsername"));
                            p.setTimestamp(((Timestamp) mp.get("timestamp")).toDate());

                            postAuthor.setText(p.getAuthorUsername());
                            postTitle.setText(p.getTitle());
                            threadCategory.setText((String) mp.get("category"));

                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                            postDate.setText(sdf.format(p.getTimestamp()));

                            markwon.setMarkdown(postContent, p.getContent());
                        }
                    }
                });
    }
    private void fetchPosts(String parentId) {
        posts = new ArrayList<>();
        db.collection("forumPosts")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ForumPost p = new ForumPost();
                                p.setId(document.getId());
                                p.setContent(document.getString("content"));
                                p.setAuthorId(document.getString("authorId"));
                                p.setAuthorUsername(document.getString("authorUsername"));
                                posts.add(p);
                            }

                            RecyclerView comments = findViewById(R.id.comments);
                            RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(ForumPostActivity.this);
                            comments.setLayoutManager(layoutManger);
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            ForumPostsAdapter adapter = new ForumPostsAdapter(posts, ForumPostActivity.this, storage);
                            comments.setAdapter(adapter);
                        } else {
                        }
                    }
                });
    }
}
