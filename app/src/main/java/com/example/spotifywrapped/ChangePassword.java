package com.example.spotifywrapped;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword {

    private final Context context;
    private static AlertDialog alert;

    public ChangePassword(Context context) {
        this.context = context;
    }

    public void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ConfirmEmailChange");
        builder.setMessage("Are you sure you want to change Password?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ChangePasswordDialogFragment confirmDialog = new ChangePasswordDialogFragment();
                confirmDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "confirmPasswordChange");
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert = builder.create();
        alert.show();
    }

    public static void reAuthenticate(String email, String password, String newPassword, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // On successful re-authentication, proceed to delete the account
                            setEmail(context, newPassword);
                        } else {
                            Toast.makeText(context, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private static void setEmail(Context context, String newPassword) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "password has been reset.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "failed to change password.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // DialogFragment for confirming account deletion with password
    public static class ChangePasswordDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_change_password, null);

            final EditText emailInput = view.findViewById(R.id.email);
            final EditText passwordInput = view.findViewById(R.id.current_password);

            final EditText newPasswordInput1 = view.findViewById(R.id.new_password);
            final EditText newPasswordInput2 = view.findViewById(R.id.reconfirm_password);

            builder.setView(view)
                    .setTitle("Enter Credentials to Confirm Email Change")
                    .setPositiveButton("Confirm", (dialog, id) -> {
                        String email = emailInput.getText().toString();
                        String password = passwordInput.getText().toString();
                        String newPassword1 = newPasswordInput1.getText().toString();
                        String newPassword2 = newPasswordInput2.getText().toString();

                        if (email.length() == 0 || password.length() == 0) {
                            Toast.makeText(getContext(), "No credentials entered", Toast.LENGTH_SHORT).show();
                        } else if (!newPassword1.equals(newPassword2)) {
                            Toast.makeText(getContext(), "new passwords don't match", Toast.LENGTH_SHORT).show();
                        } else if (newPassword1.length() < 6) {
                            Toast.makeText(getContext(), "password has to be atleast 6 characters", Toast.LENGTH_SHORT).show();
                        } else {
                            reAuthenticate(email, password, newPassword1, getContext());
                        }
                    });

            return builder.create();
        }
    }
}