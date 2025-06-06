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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsewapp.R;
import com.example.fatsewapp.models.Post;
import com.example.fatsewapp.models.Project;
import com.example.fatsewapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {

    private Context context;
    private List<Project> projectList;
    private OnProjectClickListener listener;

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    public ProjectsAdapter(Context context, List<Project> projectList, OnProjectClickListener listener) {
        this.context = context;
        this.projectList = projectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    private void shareProjectAsPost(Project project) {
        // Debug: Check if project has image data
        if (project.getImageBase64() == null || project.getImageBase64().isEmpty()) {

            Toast.makeText(context, "Project has no image to share", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        String postId = postsRef.push().getKey();

        // Create post with ALL required fields including image
        Post post = new Post();
        post.setPostId(postId);
        post.setProjectId(project.getProjectId());
        post.setUserId(userId);
        post.setTitle(project.getTitle());
        post.setDescription(project.getDescription());
        post.setImageBase64(project.getImageBase64()); // Make sure this is set
        post.setTimestamp(System.currentTimeMillis());
        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setLikes(new ArrayList<>());

        // Load username first
        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            post.setUsername(user.getUsername());
                        }

                        // Save post after setting username
                        postsRef.child(userId).child(postId).setValue(post)
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(context, "Project shared!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(context, "Sharing failed", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        // Set project title
        holder.tvTitle.setText(project.getTitle());

        // Set project description (trim if too long)
        String description = project.getDescription();
        if (description != null && description.length() > 100) {
            description = description.substring(0, 100) + "...";
        }
        holder.tvDescription.setText(description);

        // Set formatted date
//        String dateString = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//                .format(new Date(project.getCreatedAt()));
//        holder.tvDate.setText(dateString);

        // Load project image from Base64
        if (project.getImageBase64() != null && !project.getImageBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(project.getImageBase64(), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.ivProjectImage.setImageBitmap(decodedBitmap);
        } else {
            holder.ivProjectImage.setImageResource(R.drawable.ic_logout);
        }

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProjectClick(project);
            }
        });




        holder.btnShare.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Share Project")
                    .setMessage("Do you want to share this project as a post?")
                    .setPositiveButton("Share", (dialog, which) -> {
                        shareProjectAsPost(project);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public void updateProjects(List<Project> newProjects) {
        projectList.clear();
        projectList.addAll(newProjects);
        notifyDataSetChanged();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        public View btnShare,btnSelectImage;
        CardView cardView;
        ImageView ivProjectImage;
        TextView tvTitle, tvDescription;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivProjectImage = itemView.findViewById(R.id.ivProjectImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            btnSelectImage= itemView.findViewById(R.id.btnSelectImage);
            btnShare= itemView.findViewById(R.id.btnShare);


        }
    }
}