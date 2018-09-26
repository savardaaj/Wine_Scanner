package alex.winescanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Context ctx;

    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = this;

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

    }

    public void onClickLogin(View v) {
        Log.d("***DEBUG***", "Inside onClickLogin");

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if(!username.equals("") && !password.equals("")) {
            //Check that the account exists
            //query accounts table
            signIn(username, password);
        }

    }

    public void onClickGoogle(View v) {

    }

    public void onClickFacebook(View v) {

    }

    public void onClickCreateAccount(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View view = getLayoutInflater().inflate(R.layout.dialog_signin, null);

        alertDialogBuilder.setView(view);

        final LinearLayout ll = view.findViewById(R.id.ll_dialog_container);

        alertDialogBuilder.setTitle("Create Account")
            .setCancelable(true)
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    final EditText etUsername = ll.findViewById(R.id.et_dialog_username);
                    final EditText etPassword = ll.findViewById(R.id.et_dialog_password);
                    final EditText etConfirmPassword = ll.findViewById(R.id.et_dialog_confirm_password);

                    String user = etUsername.getText().toString();
                    String pass = etPassword.getText().toString();
                    String confirmPass = etConfirmPassword.getText().toString();

                    if(!user.equals("") && !pass.equals("") && !confirmPass.equals("") && pass.equals(confirmPass)) {
                        createAccount(user, pass);
                    }
                    else {
                        Toast.makeText(ctx, "Username and Password must be populated", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void createAccount(String username, String password) {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d("***LOG***", createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("***LOG***", "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("***LOG***", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //proceed to MainActivity new intent
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("***LOG***", "signInWithEmail:failure", task.getException());
                            Toast.makeText(ctx, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }

}
