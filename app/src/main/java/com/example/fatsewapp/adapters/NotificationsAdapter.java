package com.example.fatsewapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.fatsewapp.models.Notification;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fatsewapp.R;
import com.example.fatsewapp.models.User;
import com.example.fatsewapp.social.NotificationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
    private Context context;
    private List<Notification> notificationList;

    public NotificationsAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList != null ? notificationList : new ArrayList<>();
    }



    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        // Load user info who triggered the notification
        loadUserInfo(String.valueOf(notification.getFromUserId()), holder, notification);
    }

    private void loadUserInfo(String userId, NotificationViewHolder holder, Notification notification) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    holder.tvUsername.setText(user.getUsername());
                    if (user.getProfileImageUrl() != null) {
                        Glide.with(context)
                                .load(user.getProfileImageUrl())
                                .into(holder.ivProfile);
                    }

                    // Set notification text
                    holder.tvNotification.setText(notification.getText());

                    // Set click listener to open the post
//                    holder.itemView.setOnClickListener(v -> {
//                        if (notification.isPost()) {
//                            Intent intent = new Intent(context, PostDetailActivity.class);
//                            intent.putExtra("postId", notification.getPostId());
//                            intent.putExtra("userId", notification.getFromUserId());
//                            context.startActivity(intent);
//                        }
//                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationsAdapter", "Error loading user info: " + databaseError.getMessage());
            }
        });
    }



//
//    @NonNull
//    @Override
//    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
//        return new NotificationViewHolder(view);
//    }




    // Update method to safely update data
    public void updateNotifications(List<Notification> newNotifications) {
        this.notificationList = newNotifications != null ? newNotifications : new ArrayList<>();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvUsername, tvNotification;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvNotification = itemView.findViewById(R.id.tvNotification);
        }
    }
}
