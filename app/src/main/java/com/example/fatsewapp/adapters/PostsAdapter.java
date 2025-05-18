package com.example.fatsewapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.fatsewapp.R;
import com.example.fatsewapp.models.Post;
import com.example.fatsewapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private final Context context;
    private List<Post> postList;
    private final DatabaseReference usersRef;
    private final DatabaseReference postsRef;
    private String currentUserId;
    private boolean showDeleteOption;

    public PostsAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList != null ? postList : new ArrayList<>();
        this.postsRef = FirebaseDatabase.getInstance().getReference("posts");
        this.currentUserId = currentUserId;
        this.showDeleteOption = showDeleteOption;
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        if (post == null) return;

        // Set post content
        holder.tvTitle.setText(post.getTitle());
        holder.tvDescription.setText(post.getDescription());
        holder.tvLikes.setText(String.valueOf(post.getLikesCount()));
        holder.tvComments.setText(String.valueOf(post.getCommentsCount()));

        // Load user data
        loadUserInfo(post.getUserId(), holder);

        // Handle post image
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            loadImageWithGlide(post.getImageUrl(), holder.ivPostImage);
            holder.ivPostImage.setVisibility(View.VISIBLE);
        } else if (post.getImageBase64() != null && !post.getImageBase64().isEmpty()) {
            loadBase64Image(post.getImageBase64(), holder.ivPostImage);
            holder.ivPostImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }
        if (showDeleteOption && post.getUserId() != null && post.getUserId().equals(currentUserId)) {
            holder.ibDelete.setVisibility(View.VISIBLE);
            holder.ibDelete.setOnClickListener(v -> confirmAndDeletePost(post, position));
        } else {
            holder.ibDelete.setVisibility(View.VISIBLE);
            holder.ibDelete.setOnClickListener(v -> confirmAndDeletePost(post, position));
        }
    }
    private void confirmAndDeletePost(Post post, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> deletePost(post, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePost(Post post, int position) {
        if (post.getUserId() == null || post.getPostId() == null) {
            Toast.makeText(context, "Cannot delete post", Toast.LENGTH_SHORT).show();
            return;
        }

        postsRef.child(post.getUserId()).child(post.getPostId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Remove from local list and notify adapter
                    postList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
                    Log.e("PostsAdapter", "Error deleting post", e);
                });
    }
    private void loadUserInfo(String userId, PostViewHolder holder) {
        if (userId == null || userId.isEmpty()) {
            holder.tvName.setText("Anonymous");
            holder.ivProfileImage.setImageResource(R.drawable.ic_profile);
            return;
        }

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    holder.tvName.setText(user.getUsername());
                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(context)
                                .load(user.getProfileImageUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile)
                                .into(holder.ivProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.tvName.setText("Anonymous");
                holder.ivProfileImage.setImageResource(R.drawable.ic_profile);
            }
        });
    }

    private void loadImageWithGlide(String imageUrl, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners(16));

        Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)

                .into(imageView);
    }

    private void loadBase64Image(String base64String, ImageView imageView) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.postList = newPosts != null ? newPosts : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage, ivPostImage;
        TextView tvName, tvTitle, tvDescription, tvLikes, tvComments;
        ImageButton ibDelete;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLikes = itemView.findViewById(R.id.tvLikeCount);
            tvComments = itemView.findViewById(R.id.tvCommentCount);
            ibDelete = itemView.findViewById(R.id.ibDelete); // Add this
        }
    }

}