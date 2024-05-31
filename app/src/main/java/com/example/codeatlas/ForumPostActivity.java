package com.example.codeatlas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.noties.markwon.Markwon;

public class ForumPostActivity extends BaseActivity {

    ForumPost post;
    TextView postTitle, postContent, postAuthor, threadCategory, postDate;
    ArrayList<ForumPost> posts;
    Markwon markwon;
    FirebaseFirestore db;
    FirebaseStorage storage;
    String id;
    CircleImageView postAuthorPfp, userPfp;
    EditText commentInput;
    ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post);
        setupBackButton();

        id = getIntent().getExtras().getString("id", "An");

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        markwon = Markwon.builder(this).build();
        initComponents();
        fetchPost(id);
        fetchPosts(id);

        setupCommentBox();
    }

    private void initComponents(){
        postTitle = findViewById(R.id.postTitle);
        postAuthor = findViewById(R.id.authorUsername);
        threadCategory = findViewById(R.id.categoryName);
        postContent = findViewById(R.id.postContent);
        postDate = findViewById(R.id.postDate);
        commentInput = findViewById(R.id.comment_input);
        sendButton = findViewById(R.id.send_button);

        postAuthorPfp = findViewById(R.id.authorProfilePicture);
        userPfp = findViewById(R.id.userImage);
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

                            post = p;
                            User author = new User();
                            author.setId(p.getAuthorId());

                            PicassoHelper.setProfilePictureInto(postAuthorPfp, author, storage);

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

    private void setupCommentBox(){
        commentInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showKeyboard(v);
            }
        });

        initCommentProfilePicture();

        // send
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String body = String.valueOf(commentInput.getText());
                        commentInput.setText("");

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
                        data.put("title", "");

                        data.put("parentId", post.getId());
                        data.put("timestamp", timestamp);

                        db.collection("forumPosts")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(ForumPostActivity.this, "Comment added!", Toast.LENGTH_SHORT).show();
                                        hideKeyboard(v);
                                    }
                                })
                                .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));
                        fetchPosts(id);
                    }
                });
            }
        });
    }

    private void showKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void initCommentProfilePicture(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        User user = new User();
        user.setId(uid);

        PicassoHelper.setProfilePictureInto(userPfp, user, storage);
    }
}
