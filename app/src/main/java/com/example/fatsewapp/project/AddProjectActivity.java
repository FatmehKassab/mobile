package com.example.fatsewapp.project;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
import com.example.fatsewapp.R;
import com.example.fatsewapp.models.Project;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import android.content.Intent;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.widget.ImageView;
//
//
//public class AddProjectActivity extends AppCompatActivity {
//
//    private EditText titleInput, startInput, endInput, imageUrlInput;
//    private Button saveBtn;
//    private DatabaseReference dbRef;
//    private static final int PICK_IMAGE_REQUEST = 1;
//    private Uri imageUri;
//    private ImageView imagePreview;
//    private Button pickImageBtn;
//
//    private void openGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageUri = data.getData();
//            imagePreview.setImageURI(imageUri);
//        }
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_project);
//
//        titleInput = findViewById(R.id.titleInput);
//        startInput = findViewById(R.id.startInput);
//        endInput = findViewById(R.id.endInput);
//        saveBtn = findViewById(R.id.saveBtn);
//        imagePreview = findViewById(R.id.imagePreview);
//        pickImageBtn = findViewById(R.id.pickImageBtn);
//
//        dbRef = FirebaseDatabase.getInstance().getReference("projects");
//
//        pickImageBtn.setOnClickListener(v -> openGallery());
//
//        saveBtn.setOnClickListener(v -> {
//            String id = dbRef.push().getKey();
//
//            String imageUrl = (imageUri != null) ? imageUri.toString() : "";
//
//            Project project = new Project(
//                    id,
//                    titleInput.getText().toString(),
//                    startInput.getText().toString(),
//                    endInput.getText().toString(),
//                    imageUrl
//            );
//
//            dbRef.child(id).setValue(project).addOnSuccessListener(unused ->
//                    Toast.makeText(this, "Project saved", Toast.LENGTH_SHORT).show());
//        });
//
//    }
//}



import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AddProjectActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAX_IMAGE_SIZE = 1024; // Max width/height in pixels
    private static final int QUALITY = 70; // Image quality percentage

    private EditText etTitle, etDescription, etPattern, etYarnDetails;
    private Button btnAddProject, btnSelectImage;
    private ImageView ivProjectImage;
    private Bitmap selectedImage;
    private FirebaseAuth mAuth;
    private DatabaseReference projectsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        mAuth = FirebaseAuth.getInstance();
        projectsRef = FirebaseDatabase.getInstance().getReference("projects");

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etPattern = findViewById(R.id.etPattern);
        etYarnDetails = findViewById(R.id.etYarnDetails);
        btnAddProject = findViewById(R.id.btnAddProject);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivProjectImage = findViewById(R.id.ivProjectImage);

        btnSelectImage.setOnClickListener(v -> openFileChooser());
        btnAddProject.setOnClickListener(v -> addProject());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

                // Resize image to reduce size
                int width = originalBitmap.getWidth();
                int height = originalBitmap.getHeight();

                float ratio = (float) width / (float) height;
                if (ratio > 1) {
                    width = MAX_IMAGE_SIZE;
                    height = (int) (width / ratio);
                } else {
                    height = MAX_IMAGE_SIZE;
                    width = (int) (height * ratio);
                }

                selectedImage = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
                ivProjectImage.setImageBitmap(selectedImage);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addProject() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String pattern = etPattern.getText().toString().trim();
        String yarnDetails = etYarnDetails.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            return;
        }

        String projectId = projectsRef.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();
        long createdAt = System.currentTimeMillis();

        String imageBase64 = "";
        if (selectedImage != null) {
            imageBase64 = convertBitmapToBase64(selectedImage);
        }

        Project project = new Project();
        project.setProjectId(projectId);
        project.setUserId(userId);
        project.setTitle(title);
        project.setDescription(description);
        project.setPattern(pattern);
        project.setYarnDetails(yarnDetails);
        project.setImageBase64(imageBase64);
        project.setCreatedAt(createdAt);
        project.setPublic(false); // Default to private

        projectsRef.child(userId).child(projectId).setValue(project)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Project added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddProjectActivity.this, MyProjectsActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Failed to add project", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}