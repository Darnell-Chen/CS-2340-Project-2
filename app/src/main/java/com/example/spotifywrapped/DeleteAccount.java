package com.example.spotifywrapped;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccount {

    private final Context context;

    public DeleteAccount(Context context) {
        this.context = context;
    }

    public void terminateAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ConfirmDeleteDialogFragment confirmDialog = new ConfirmDeleteDialogFragment();
                confirmDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "confirmDelete");
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private void reAuthenticate(String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234");

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        currentUser.delete();

                        Toast.makeText(context, "Account has been deleted", Toast.LENGTH_SHORT).show();

//                        dialog.dismiss();
                        logout();
                    }
                });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent loginPage = new Intent(context, MainActivity.class);
        loginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(loginPage);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    // DialogFragment for confirming account deletion with password
    public static class ConfirmDeleteDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_confirm_delete, null);
            final EditText passwordInput = view.findViewById(R.id.password);

            builder.setView(view)
                    .setTitle("Enter Password to Confirm Account Deletion")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String password = passwordInput.getText().toString();
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            // TODO: Add password validation logic here

                            System.out.println(password);

                            // If password validation succeeds
//                            if (currentUser != null) {
//                                currentUser.delete().addOnCompleteListener(task -> {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(getContext(), "Account has been deleted", Toast.LENGTH_SHORT).show();
//                                        logout();
//                                    } else {
//                                        Toast.makeText(getContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
                        }
                    });

            return builder.create();
        }

        private void logout() {
            FirebaseAuth.getInstance().signOut();
            Activity activity = getActivity();
            if (activity != null) {
                Intent loginPage = new Intent(activity, MainActivity.class);
                loginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(loginPage);
                activity.finish();
            }
        }
    }
}