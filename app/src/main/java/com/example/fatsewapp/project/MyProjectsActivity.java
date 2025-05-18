package com.example.fatsewapp.project;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fatsewapp.R;
import com.example.fatsewapp.adapters.ProjectsAdapter;
import com.example.fatsewapp.models.Project;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MyProjectsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProjectsAdapter adapter;
    private List<Project> projectList;
    private DatabaseReference projectsRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_projects);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        projectsRef = FirebaseDatabase.getInstance().getReference("projects").child(userId);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        projectList = new ArrayList<>();

        // ✅ Proper adapter initialization
        adapter = new ProjectsAdapter(this, projectList, project -> {
            Intent intent = new Intent(MyProjectsActivity.this, AddProjectActivity.class);
            intent.putExtra("projectId", project.getProjectId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // ✅ Now safe to load data
        loadProjects();
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fabAddProject = findViewById(R.id.fabAddProject);
        fabAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ProfileActivity
                Intent intent = new Intent(MyProjectsActivity.this, AddProjectActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loadProjects() {
        projectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                projectList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Project project = snapshot.getValue(Project.class);
                    projectList.add(project);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}