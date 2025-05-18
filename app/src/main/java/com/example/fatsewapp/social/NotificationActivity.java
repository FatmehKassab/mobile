package com.example.fatsewapp.social;

import com.example.fatsewapp.models.Notification;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsewapp.R;
import com.example.fatsewapp.adapters.NotificationsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<Notification> notificationList;
    private String currentUserId;
    private DatabaseReference notificationsRef;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        adapter = new NotificationsAdapter(this, notificationList);
        recyclerView.setAdapter(adapter);

        loadNotifications();
    }

    // In your NotificationsActivity:
    private void loadNotifications() {
        notificationsRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    if (notification != null) {
                        notifications.add(0, notification);
                    }
                }
                adapter.updateNotifications(notifications); // Use the safe update method
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationsActivity", "Error loading notifications", databaseError.toException());
                adapter.updateNotifications(new ArrayList<>()); // Set empty list on error
            }
        });
    }
}