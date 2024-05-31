package com.example.codeatlas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardActivity extends BaseActivity {

    FirebaseFirestore db;
    FirebaseStorage storage;
    private ArrayList<User> users;
    CircleImageView imageViewFirst, imageViewSecond, imageViewThird;
    TextView usernameFirst, usernameSecond, usernameThird, pointsFirst, pointsSecond, pointsThird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Navbar.initNavBar(LeaderboardActivity.this);
        ButtonsAlpha.resetButtonsAlpha(this);
        ButtonsAlpha.leaderboard.setAlpha(1f);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        setupBackButton();
        fetchUsers();
        initLayoutComponents();
    }

    public void initLayoutComponents(){
        imageViewFirst = findViewById(R.id.pictureFirstRank);
        imageViewSecond = findViewById(R.id.pictureSecondRank);
        imageViewThird = findViewById(R.id.pictureThirdRank);

        usernameFirst = findViewById(R.id.firstNameText);
        usernameSecond = findViewById(R.id.secondNameText);
        usernameThird = findViewById(R.id.thirdNameText);

        pointsFirst = findViewById(R.id.firstPoints);
        pointsSecond = findViewById(R.id.secondPoints);
        pointsThird = findViewById(R.id.thirdPoints);
    }

    public void fetchUsers(){
        users = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> mp =  document.getData();
                                User user = new User();
                                user.setId(document.getId());
                                user.setUsername((String) mp.get("username"));
                                user.setStars((Long) mp.get("stars"));

                                users.add(user);
                            }

                            Collections.sort(users, new Comparator<User>() {
                                @Override
                                public int compare(User u1, User u2) {
                                    return Long.compare(u2.getStars(), u1.getStars());
                                }
                            });

                            ArrayList<User> temp = new ArrayList<>();
                            for(int i = 0; i < Math.min(13, users.size()); i++){
                                temp.add(users.get(i));
                            }

                            users = temp;
                            // init first
                            pointsFirst.setText(String.valueOf(users.get(0).getStars()));
                            usernameFirst.setText(users.get(0).getUsername());
                            PicassoHelper.setProfilePictureInto(imageViewFirst, users.get(0), storage);
                            users.remove(0);

                            // init second
                            pointsSecond.setText(String.valueOf(users.get(0).getStars()));
                            usernameSecond.setText(users.get(0).getUsername());
                            PicassoHelper.setProfilePictureInto(imageViewSecond, users.get(0), storage);
                            users.remove(0);

                            // init third
                            pointsThird.setText(String.valueOf(users.get(0).getStars()));
                            usernameThird.setText(users.get(0).getUsername());
                            PicassoHelper.setProfilePictureInto(imageViewThird, users.get(0), storage);
                            users.remove(0);

                            RecyclerView leaderboard = findViewById(R.id.recyclerView);
                            RecyclerView.LayoutManager layoutManger = new LinearLayoutManager(LeaderboardActivity.this);
                            leaderboard.setLayoutManager(layoutManger);
                            LeaderboardAdapter adapter = new LeaderboardAdapter(users, storage);
                            leaderboard.setAdapter(adapter);
                        } else {
                        }
                    }
                });
    }

}