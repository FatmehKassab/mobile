package com.example.fatsewapp.social;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fatsewapp.R;
import com.example.fatsewapp.adapters.PostsAdapter;
import com.example.fatsewapp.auth.LoginActivity;
import com.example.fatsewapp.models.Post;
import com.example.fatsewapp.models.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUserName, tvEmail, tvToolbarUsername, tvPostsCount;
    private ImageView ivProfileImage, ivLogout;
    private RecyclerView recyclerView;
    private PostsAdapter adapter;
    private List<Post> postList;
    private DatabaseReference userRef, postsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        postsRef = FirebaseDatabase.getInstance().getReference("posts").child(userId);

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Set default profile icon
        ivProfileImage.setImageResource(R.drawable.ic_profile);

        // Setup logout button
        ivLogout.setOnClickListener(v -> logoutUser());

        // Setup RecyclerView
        setupRecyclerView();

        // Load data
        loadUserData();
        loadPosts();
    }

    private void initializeViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvToolbarUsername = findViewById(R.id.tvToolbarUsername);
        tvPostsCount = findViewById(R.id.tvPostsCount);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivLogout = findViewById(R.id.ivLogout);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        adapter = new PostsAdapter(this, postList);
        recyclerView.setAdapter(adapter);
    }

    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    // Update UI with user data
                    tvUserName.setText(user.getUsername());
                    tvEmail.setText(user.getEmail());
                    tvToolbarUsername.setText(user.getUsername());

                    // Load profile image if available
                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(user.getProfileImageUrl())
                                .placeholder(R.drawable.ic_profile)
                                .into(ivProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(snapshot.getKey());
                        // Make sure image URL is being set
                        if (snapshot.hasChild("imageUrl")) {
                            post.setImageUrl(snapshot.child("imageUrl").getValue(String.class));
                        }
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when activity resumes
        loadUserData();
        loadPosts();
    }
}