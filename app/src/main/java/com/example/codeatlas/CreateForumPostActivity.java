package com.example.codeatlas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateForumPostActivity extends AppCompatActivity {
    String categoryId, categoryTitle;

    TextView threadTitleView;
    EditText titleEditText, bodyEditText;
    Button sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_forum_post_activity);

        categoryId = getIntent().getExtras().getString("categoryId");
        categoryTitle = getIntent().getExtras().getString("categoryTitle");

        threadTitleView = findViewById(R.id.newThreadHeader);
        threadTitleView.setText(categoryTitle);

        sendButton = findViewById(R.id.sendButton);

        titleEditText = findViewById(R.id.titleText);
        bodyEditText = findViewById(R.id.bodyText);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = String.valueOf(titleEditText.getText());
                String body = String.valueOf(bodyEditText.getText());

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                String uid = user.getUid();
                String username = user.getDisplayName();
                Timestamp timestamp = new Timestamp(new Date());

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> data = new HashMap<>();
                data.put("authorId", uid);
                data.put("authorUsername", username);
                data.put("content", body);
                data.put("title", title);
                data.put("category", categoryTitle);
                data.put("parentId", categoryId);
                data.put("timestamp", timestamp);

                db.collection("forumPosts")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(CreateForumPostActivity.this, "Post Created!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateForumPostActivity.this, ThreadsActivity.class);
                                intent.putExtra("categoryId", categoryId);
                                intent.putExtra("categoryTitle", categoryTitle);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));
            }
        });
    }
}
