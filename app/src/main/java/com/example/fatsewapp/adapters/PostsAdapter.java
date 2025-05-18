package com.example.fatsewapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fatsewapp.R;
import com.example.fatsewapp.models.Post;
import com.example.fatsewapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private Context context;
    private List<Post> postList;
    private FirebaseAuth mAuth;

    public PostsAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
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

        // Set default profile image
        holder.ivProfileImage.setImageResource(R.drawable.ic_profile);
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        // Load user data
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);;
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    holder.tvName.setText(user.getUsername());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        holder.tvTitle.setText(post.getTitle());
        holder.tvDescription.setText(post.getDescription());
        holder.tvLikes.setText(String.valueOf(post.getLikesCount()));
        holder.tvComments.setText(String.valueOf(post.getCommentsCount()));

        // Load post image if available
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(post.getImageUrl())

                    .into(holder.ivPostImage);
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage, ivPostImage;
        public TextView tvName;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvLikes;
        TextView tvComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLikes = itemView.findViewById(R.id.tvLikeCount);
            tvComments = itemView.findViewById(R.id.tvCommentCount);
        }
    }
}