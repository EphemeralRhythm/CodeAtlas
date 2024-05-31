package com.example.codeatlas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ThreadsActivity extends BaseActivity {
    ArrayList<ForumPost> posts;
    TextView threadTitleView, sortByView;
    FirebaseFirestore db;
    ImageButton createThreadButton;
    String threadId, threadTitle;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) view.getTag();
            int position = vh.getAdapterPosition();
            ForumPost post = posts.get(position);

            Intent intent = new Intent(ThreadsActivity.this, ForumPostActivity.class);
            intent.putExtra("id", post.getId());
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threads);

        setupBackButton();

        threadId = getIntent().getExtras().getString("categoryId", "An");
        threadTitle = getIntent().getExtras().getString("categoryTitle", "Announcements");

        threadTitleView = findViewById(R.id.threadTitle);
        sortByView = findViewById(R.id.sortBy);

        posts = new ArrayList<>();

        createThreadButton = findViewById(R.id.newThreadButton);
        threadTitleView.setText(threadTitle);
        db = FirebaseFirestore.getInstance();

        initButton();
        Navbar.initNavBar(this);
        ButtonsAlpha.resetButtonsAlpha(this);
        ButtonsAlpha.community.setAlpha(1f);
        fetchPosts(threadId);
    }


    private void fetchPosts(String parentId) {
        db.collection("forumPosts")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> mp =  document.getData();
                                ForumPost p = new ForumPost();
                                p.setId(document.getId());
                                p.setTitle((String) mp.get("title"));
                                p.setContent((String) mp.get("content"));
                                p.setAuthorId((String) mp.get("authorId"));
                                p.setAuthorUsername((String) mp.get("authorUsername"));

                                posts.add(p);
                            }

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            RecyclerView threads = findViewById(R.id.threads);
                            RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(ThreadsActivity.this);
                            threads.setLayoutManager(layoutManger);
                            ThreadsAdapter adapter = new ThreadsAdapter(posts, ThreadsActivity.this, storage);
                            adapter.setOnClickListener(listener);
                            threads.setAdapter(adapter);
                        } else {
                        }
                    }
                });
    }


    public void initButton() {
        createThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Intent intent = new Intent(ThreadsActivity.this, CreateForumPostActivity.class);
                intent.putExtra("categoryId", threadId);
                intent.putExtra("categoryTitle", threadTitle);
                startActivity(intent);
            }
        });
    }
}
