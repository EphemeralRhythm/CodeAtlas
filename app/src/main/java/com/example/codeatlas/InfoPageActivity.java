package com.example.codeatlas;

import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.commonmark.node.Node;

import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;

public class InfoPageActivity extends AppCompatActivity implements LevelInterface{

    String track, topic;
    int level, pageNumber;
    TextView contentView;
    ImageButton nextButton;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView heartsView;
    int hearts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_page);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initLayoutComponents();
        initPageInfo();
        initNextButton();
        MapLevelHelper.initTopBar(db, InfoPageActivity.this);
        MapLevelHelper.fetchLevelData(db, track, topic, level, pageNumber, this);
    }

    public void initPageInfo(){
        track = getIntent().getExtras().getString("track");
        topic = getIntent().getExtras().getString("topic");
        level = getIntent().getExtras().getInt("level");
        pageNumber = getIntent().getExtras().getInt("page");

        String uid = mAuth.getUid();
        DocumentReference ref = db.collection("users").document(uid);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                hearts = Math.toIntExact(task.getResult().getLong("hearts"));
                heartsView.setText(String.valueOf(hearts));
            }
        });
    }
    public void initLayoutComponents(){
        contentView = findViewById(R.id.contentView);
        nextButton = findViewById(R.id.nextButton);
        heartsView = findViewById(R.id.livesText) ;
    }
    public void updateUI(Page page){
        Markwon markwon = Markwon.builder(this).build();
        markwon.setMarkdown(contentView, page.getContent().replace("\\n", "\n"));
    }

    public void initNextButton(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapLevelHelper.nextPage(db, track, topic, level,
                        pageNumber + 1, InfoPageActivity.this, false, getSupportFragmentManager());
            }
        });
    }
}
