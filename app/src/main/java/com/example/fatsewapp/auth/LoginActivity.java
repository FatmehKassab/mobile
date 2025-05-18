package com.example.fatsewapp.auth;
//import androidx.appcompat.app.AppCompatActivity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
import com.example.fatsewapp.MainActivity;
import com.example.fatsewapp.R;
import com.example.fatsewapp.models.User;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class LoginActivity extends AppCompatActivity {
//
//    EditText emailEt, passwordEt;
//    Button loginBtn;
//    DatabaseReference dbRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        TextView registerLink = findViewById(R.id.registerLink);
//
//        registerLink.setOnClickListener(v -> {
//
//            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//
//        });
//
//        emailEt = findViewById(R.id.email);
//        passwordEt = findViewById(R.id.password);
//        loginBtn = findViewById(R.id.btnLogin);
//        dbRef = FirebaseDatabase.getInstance().getReference("users");
//
//        loginBtn.setOnClickListener(v -> {
//            String email = emailEt.getText().toString().replace(".", "_");
//            String password = passwordEt.getText().toString();
//
//            dbRef.child(email).get().addOnCompleteListener(task -> {
//                if (task.isSuccessful() && task.getResult().exists()) {
//                    User user = task.getResult().getValue(User.class);
//                    if (user.password.equals(password)) {
//
//                        // ✅ Save the user's name in SharedPreferences
//                        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putString("username", user.name); // or user.email if you want
//                        editor.apply();
//
//                        // ✅ Proceed to Dashboard
//                        Intent intent = new Intent(this, DashboardActivity.class);
//                        startActivity(intent);
//                        finish();
//
//                    } else {
//                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
//
//
//    }
//}


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginUser());
        Button registerBtn = findViewById(R.id.registerButton);
        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(userId)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    if (snapshot.exists()) {
                                        User user = snapshot.getValue(User.class);

                                        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("username", user.getUsername()); // Make sure 'getName()' exists in your User model
                                        editor.apply();

                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


}