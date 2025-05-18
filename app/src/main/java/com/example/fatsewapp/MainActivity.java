package com.example.fatsewapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fatsewapp.models.User;
import com.example.fatsewapp.project.MyProjectsActivity;
import com.example.fatsewapp.social.CommunityActivity;

import com.example.fatsewapp.social.NotificationActivity;
import com.example.fatsewapp.social.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView welcomeTextView = findViewById(R.id.tvWelcomeUser);

        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users") // adjust this path if needed
                    .child(uid);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        welcomeTextView.setText("Welcome, " + user.getUsername() + "!");
                    } else {
                        welcomeTextView.setText("Welcome!");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    welcomeTextView.setText("Welcome!");
                }
            });
        } else {
            welcomeTextView.setText("Welcome!");
        }

        // Notification bell in header
        ImageView btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        // Projects button
        LinearLayout btnPlanProjects = findViewById(R.id.btnPlanProjects);
        btnPlanProjects.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyProjectsActivity.class);
            startActivity(intent);
        });

        // Community button
        LinearLayout btnCommunity = findViewById(R.id.btnCommunity);
        btnCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
            startActivity(intent);
        });

        // Profile button
        LinearLayout btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }


}
