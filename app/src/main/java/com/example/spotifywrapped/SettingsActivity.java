package com.example.spotifywrapped;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvBack;
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        tvBack = findViewById(R.id.settingsBack);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, DashboardActivity.class));
                finish();
            }
        });

        Button changePasswordButton = findViewById(R.id.ChangePassword);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePassword changePasswordDialog = new ChangePassword(context);
                changePasswordDialog.changePassword();
            }
        });

        Button changeEmailButton = findViewById(R.id.change_email);
        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeEmail changeEmailDialog = new ChangeEmail(context);
                changeEmailDialog.changeEmail();
            }
        });
    }

    public void deleteAccount(View view) {
        DeleteAccount delete = new DeleteAccount(SettingsActivity.this);
        delete.terminateAccount();
    }
}