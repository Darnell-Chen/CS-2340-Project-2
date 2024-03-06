package com.example.spotifywrapped;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

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

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                currentUser.delete();

                Toast.makeText(context, "Account has been deleted", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                logout();
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

    private void logout() {
        Intent loginPage = new Intent (context, MainActivity.class);
        context.startActivity(loginPage);
    }
}
