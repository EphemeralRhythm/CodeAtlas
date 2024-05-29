package com.example.codeatlas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ForumsActivity extends BaseActivity {
    private ArrayList<ForumTitle> titles;
    private FirebaseFirestore db;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) view.getTag();
            int position = vh.getAdapterPosition();

            String id = titles.get(position).getId();
            String title = titles.get(position).getTitle();
            Intent intent = new Intent(ForumsActivity.this, ThreadsActivity.class);
            intent.putExtra("categoryId", id);
            intent.putExtra("categoryTitle", title);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums);

        setupBackButton();

        titles = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        fetchPosts();
        Navbar.initNavBar(this);
    }

    private void fetchPosts() {
        db.collection("forumTitles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> mp =  document.getData();
                                ForumTitle t = new ForumTitle();
                                t.setId(document.getId());
                                t.setDescription((String) mp.get("description"));
                                t.setColor((String) mp.get("color"));
                                t.setTitle((String) mp.get("title"));

                                titles.add(t);
                            }

                            RecyclerView forumTitles = findViewById(R.id.forum_titles);
                            RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(ForumsActivity.this);
                            forumTitles.setLayoutManager(layoutManger);
                            ForumTitleAdapter adapter = new ForumTitleAdapter(titles);
                            adapter.setOnClickListener(listener);
                            forumTitles.setAdapter(adapter);
                        } else {
                        }
                    }
                });
    }
}
