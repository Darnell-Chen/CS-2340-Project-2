package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button registerButton;
    private TextView backButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton2);
        backButton = findViewById(R.id.registerBack);

        auth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = email.getText().toString();
                String newPassword = password.getText().toString();


                // Android Studio prefers Text.Utils according to Stack Overflow
                if (TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please input credentials", Toast.LENGTH_SHORT).show();

                } else if (newPassword.length() < 6) {
                    // Note that this requirement is by Firebase - not our choice
                    Toast.makeText(RegisterActivity.this, "Password has to be at least 6 characters long", Toast.LENGTH_SHORT).show();

                } else {
                    register(newEmail, newPassword);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void register(String newEmail, String newPassword) {
        auth.createUserWithEmailAndPassword(newEmail, newPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration Succeeded!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Users").child(auth.getUid().toString()).child("AuthToken").setValue("");

                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
