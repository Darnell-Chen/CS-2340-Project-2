package com.example.spotifywrapped;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccount extends AppCompatActivity {

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

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccount.this);
        LayoutInflater inflater = LayoutInflater.from(DeleteAccount.this);
        View passwordView = inflater.inflate(R.layout.password_confirmation, null);
        builder.setView(passwordView);

        final EditText passwordEditText = passwordView.findViewById(R.id.passwordEditText);
        Button confirmButton = passwordView.findViewById(R.id.confirmButton);

        AlertDialog dialog = builder.create();
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordEditText.getText().toString();
                // TODO: Implement the logic after password is entered and CONFIRM is clicked
                dialog.dismiss();
            }
        });
    }
}
