package com.example.fatsewapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fatsewapp.R;

import com.example.fatsewapp.models.Post;
import com.example.fatsewapp.models.Notification;
import com.example.fatsewapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.PostViewHolder> {
    private Context context;
    private List<Post> postList;
    private List<Post> allPosts;
    private String currentUserId;
    private DatabaseReference postsRef;
    private DatabaseReference notificationsRef;
    private DatabaseReference usersRef;

    public CommunityAdapter(Context context, String currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.postList = new ArrayList<>();
        this.allPosts = new ArrayList<>();
        this.postsRef = FirebaseDatabase.getInstance().getReference("posts");
        this.notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
        loadPosts();
    }

    private void loadPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                allPosts.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String postUserId = userSnapshot.getKey();
                    if (postUserId == null || postUserId.equals(currentUserId)) {
                        continue;
                    }

                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        try {
                            // Skip if the value is not a Post object
                            if (!postSnapshot.hasChildren()) {
                                Log.w("CommunityAdapter", "Skipping non-Post object at: " +
                                        postSnapshot.getRef().toString());
                                continue;
                            }

                            Post post = postSnapshot.getValue(Post.class);
                            if (post != null) {
                                post.setPostId(postSnapshot.getKey());
                                post.setUserId(postUserId);
                                postList.add(post);
                                allPosts.add(post);
                            }
                        } catch (DatabaseException e) {
                            Log.e("CommunityAdapter", "Error parsing post at: " +
                                    postSnapshot.getRef().toString(), e);
                        }
                    }
                }

                if (postList.isEmpty()) {
                    Log.d("CommunityAdapter", "No posts found (excluding current user's posts)");
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CommunityAdapter", "Error loading posts: " + databaseError.getMessage());
            }
        });
    }

    public void filterPosts(String query) {
        if (TextUtils.isEmpty(query)) {
            postList.clear();
            postList.addAll(allPosts);
        } else {
            List<Post> filteredList = new ArrayList<>();
            String lowercaseQuery = query.toLowerCase();

            for (Post post : allPosts) {
                if ((post.getTitle() != null && post.getTitle().toLowerCase().contains(lowercaseQuery)) ||
                        (post.getDescription() != null && post.getDescription().toLowerCase().contains(lowercaseQuery))) {
                    filteredList.add(post);
                }
            }
            postList.clear();
            postList.addAll(filteredList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Bind post data
        holder.tvTitle.setText(post.getTitle());
        holder.tvDescription.setText(post.getDescription());

        // Handle likes
        int likeCount = Math.toIntExact(post.getLikesCount());
        holder.tvLikeCount.setText(likeCount + (likeCount == 1 ? " like" : " likes"));

        boolean isLiked = post.getLikes() != null && post.getLikes().contains(currentUserId);
        holder.ibLike.setImageResource(isLiked ? R.drawable.ic_like : R.drawable.ic_outline_like);
        holder.ibLike.setOnClickListener(v -> toggleLike(post, holder));

        // Load user info
        loadUserInfo(post.getUserId(), holder);
    }

    private void toggleLike(Post post, PostViewHolder holder) {
        DatabaseReference postRef = postsRef.child(post.getUserId()).child(post.getPostId());

        postRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.getLikes() == null) {
                    p.setLikes(new ArrayList<>());
                }

                if (p.getLikes().contains(currentUserId)) {
                    // Unlike
                    p.getLikes().remove(currentUserId);
                    p.setLikesCount(p.getLikesCount() - 1);
                } else {
                    // Like
                    p.getLikes().add(currentUserId);
                    p.setLikesCount(p.getLikesCount() + 1);

                    // Send notification if not own post
                    if (!post.getUserId().equals(currentUserId)) {
                        createLikeNotification(post.getUserId(), post.getPostId());
                    }
                }

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e("CommunityAdapter", "Like transaction failed: " + error.getMessage());
                }
            }
        });
    }

    private void createLikeNotification(String postOwnerId, String postId) {
        if (postOwnerId.equals(currentUserId)) return; // Don't notify self

        String notificationId = notificationsRef.child(postOwnerId).push().getKey();
        Notification notification = new Notification(
                currentUserId,
                "liked your post",
                postId,
                System.currentTimeMillis()
        );

        notificationsRef.child(postOwnerId).child(notificationId).setValue(notification);
    }

    private void loadUserInfo(String userId, PostViewHolder holder) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    holder.tvUsername.setText(user.getUsername());
                    if (user.getProfileImageUrl() != null) {
                        Glide.with(context)
                                .load(user.getProfileImageUrl())
                                .circleCrop()
                                .into(holder.ivProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CommunityAdapter", "Error loading user info: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvUsername, tvTitle, tvDescription, tvLikeCount;
        ImageButton ibLike;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            ibLike = itemView.findViewById(R.id.ibLike);
        }
    }
}