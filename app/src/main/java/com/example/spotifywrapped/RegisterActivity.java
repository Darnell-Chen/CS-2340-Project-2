package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button registerButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerEmail);
        registerButton = findViewById(R.id.registerButton2);

        auth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = email.getText().toString();
                String newPassword = password.getText().toString();

                // Android Studio prefers Text.Utils according to Stack Overflow
                if (TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please input credentials", Toast.LENGTH_SHORT).show();
                } else {
                    register(newEmail, newPassword);
                }
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
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
