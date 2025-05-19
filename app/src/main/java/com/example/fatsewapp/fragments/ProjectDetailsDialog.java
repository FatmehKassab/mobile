package com.example.fatsewapp.fragments;



import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fatsewapp.R;
import com.example.fatsewapp.models.Project;

public class ProjectDetailsDialog extends DialogFragment {

    private static final String ARG_PROJECT = "project";

    public static ProjectDetailsDialog newInstance(Project project) {
        ProjectDetailsDialog fragment = new ProjectDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROJECT, project);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_project_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Project project = (Project) getArguments().getSerializable(ARG_PROJECT);

        ImageView imageView = view.findViewById(R.id.dialog_project_image);
        TextView titleView = view.findViewById(R.id.dialog_project_title);
        TextView descriptionView = view.findViewById(R.id.dialog_project_description);
        TextView patternView = view.findViewById(R.id.dialog_project_pattern);
        TextView yarnDetailsView = view.findViewById(R.id.dialog_project_yarn_details);

        titleView.setText(project.getTitle());
        descriptionView.setText(project.getDescription());
        patternView.setText(project.getPattern());
        yarnDetailsView.setText(project.getYarnDetails());

        if (project.getImageBase64() != null && !project.getImageBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(project.getImageBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }

        view.findViewById(R.id.dialog_close_button).setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}