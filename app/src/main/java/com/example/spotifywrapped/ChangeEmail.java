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

public class ChangeEmail {

    private final Context context;
    private static AlertDialog alert;

    public ChangeEmail(Context context) {
        this.context = context;
    }

    public void changeEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ConfirmEmailChange");
        builder.setMessage("Are you sure you want to change Email?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ChangeEmailDialogFragment confirmDialog = new ChangeEmailDialogFragment();
                confirmDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "confirmEmailChange");
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

    public static void reAuthenticate(String email, String password, String newEmail, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // On successful re-authentication, proceed to delete the account
                            setEmail(context, newEmail);
                        } else {
                            Toast.makeText(context, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private static void setEmail(Context context, String newEmail) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Verification sent to New Email.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "failed to change email.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // DialogFragment for confirming account deletion with password
    public static class ChangeEmailDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_change_email, null);

            final EditText emailInput = view.findViewById(R.id.current_email);
            final EditText passwordInput = view.findViewById(R.id.password);

            final EditText newEmailInput1 = view.findViewById(R.id.new_email);
            final EditText newEmailInput2 = view.findViewById(R.id.reconfirm_email);

            builder.setView(view)
                    .setTitle("Enter Credentials to Confirm Email Change")
                    .setPositiveButton("Confirm", (dialog, id) -> {
                        String email = emailInput.getText().toString();
                        String password = passwordInput.getText().toString();
                        String newEmail1 = newEmailInput1.getText().toString();
                        String newEmail2 = newEmailInput2.getText().toString();

                        if (email.length() == 0 || password.length() == 0) {
                            Toast.makeText(getContext(), "No credentials entered", Toast.LENGTH_SHORT).show();
                        } else if (!newEmail1.equals(newEmail2)){
                            Toast.makeText(getContext(), "new emails don't match", Toast.LENGTH_SHORT).show();
                        } else {
                            reAuthenticate(email, password, newEmail1, getContext());
                        }
                    });

            return builder.create();
        }
    }
}