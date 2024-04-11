package com.example.spotifywrapped;

import android.content.Intent;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvBack;

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

        Button changePasswordButton = findViewById(R.id.change_password);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialogFragment changePasswordDialog = new ChangePasswordDialogFragment();
                changePasswordDialog.show(getSupportFragmentManager(), "ChangePasswordDialog");
            }
        });

        Button changeEmailButton = findViewById(R.id.change_email);
        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeEmailDialogFragment changeEmailDialog = new ChangeEmailDialogFragment();
                changeEmailDialog.show(getSupportFragmentManager(), "ChangeEmailDialog");
            }
        });
    }

    public void deleteAccount(View view) {
        DeleteAccount delete = new DeleteAccount(SettingsActivity.this);
        delete.terminateAccount();
    }

    public static class ChangePasswordDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_change_password, null);

            final EditText emailInput = view.findViewById(R.id.email);
            final EditText currentPasswordInput = view.findViewById(R.id.current_password);
            final EditText newPasswordInput = view.findViewById(R.id.new_password);

            builder.setView(view)
                    .setTitle("Change Password")
                    .setPositiveButton("Confirm", (dialog, id) -> {
                        String email = emailInput.getText().toString();
                        String currentPassword = currentPasswordInput.getText().toString();
                        String newPassword = newPasswordInput.getText().toString();
                        // Call method to change the password
                        changePassword(email, currentPassword, newPassword, getContext());
                    });

            return builder.create();
        }

        public static void changePassword(String email, String currentPassword, String newPassword, Context context) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null && user.getEmail().equals(email)) {
                AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Error password not updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public static class ChangeEmailDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_change_email, null);

            final EditText currentEmailInput = view.findViewById(R.id.current_email);
            final EditText passwordInput = view.findViewById(R.id.password);
            final EditText newEmailInput = view.findViewById(R.id.new_email);

            builder.setView(view)
                    .setTitle("Change Email")
                    .setPositiveButton("Confirm", (dialog, id) -> {
                        String currentEmail = currentEmailInput.getText().toString();
                        String password = passwordInput.getText().toString();
                        String newEmail = newEmailInput.getText().toString();
                        // Call method to change the email
                        changeEmail(currentEmail, password, newEmail, getContext());
                    });

            return builder.create();
        }

        public static void changeEmail(String currentEmail, String password, String newEmail, Context context) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null && user.getEmail().equals(currentEmail)) {
                AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updateEmail(newEmail)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(context, "Email address updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Error email not updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}