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
        if (notificationList == null || position < 0 || position >= notificationList.size()) {
            return;
        }

        Notification notification = notificationList.get(position);
        if (notification == null) {
            return;
        }

        // Load user info who triggered the notification
        if (notification.getFromUserId() != null) {
            loadUserInfo(notification.getFromUserId(), holder, notification);
        } else {
            holder.tvUsername.setText("Unknown user");
            holder.tvNotification.setText(notification.getText());
        }
    }

    private void loadUserInfo(String userId, NotificationViewHolder holder, Notification notification) {
        // Add null checks for critical parameters
        if (userId == null || userId.isEmpty()) {
            Log.e("NotificationsAdapter", "Invalid user ID");
            holder.tvUsername.setText("Unknown user");
            holder.tvNotification.setText(notification.getText());
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    // Set profile image
                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(context)
                                .load(user.getProfileImageUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile)
                                .into(holder.ivProfile);
                    } else {
                        holder.ivProfile.setImageResource(R.drawable.ic_profile);
                    }

                    // Build dynamic notification text
                    String notificationText = user.getUsername() + " " + notification.getText();
                    holder.tvNotification.setText(notificationText);

                    // Set username
                    holder.tvUsername.setText(user.getUsername());
                } else {
                    holder.tvUsername.setText("Unknown user");
                    holder.tvNotification.setText(notification.getText());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationsAdapter", "Error loading user info: " + databaseError.getMessage());
                holder.tvUsername.setText("Unknown user");
                holder.tvNotification.setText(notification.getText());
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
