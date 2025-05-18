package com.example.fatsewapp.auth;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//
//import android.widget.Toast;
//
import com.example.fatsewapp.MainActivity;
import com.example.fatsewapp.R;
import com.example.fatsewapp.models.User;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    EditText nameEt, emailEt, passwordEt;
//    Button registerBtn;
//    DatabaseReference dbRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        nameEt = findViewById(R.id.name);
//        emailEt = findViewById(R.id.email);
//        passwordEt = findViewById(R.id.password);
//        registerBtn = findViewById(R.id.registerBtn);
//        dbRef = FirebaseDatabase.getInstance().getReference("users");
//
//        registerBtn.setOnClickListener(v -> {
//            String name = nameEt.getText().toString();
//            String email = emailEt.getText().toString().replace(".", "_");
//            String password = passwordEt.getText().toString();
//
//            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
//                User user = new User(name, email, password);
//                dbRef.child(email).setValue(user).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(this, LoginActivity.class));
//                    } else {
//                        Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
//import com.example.crochetapp.MainActivity;
//import com.example.crochetapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(userId, username, email);

                        usersRef.child(userId).setValue(user)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}