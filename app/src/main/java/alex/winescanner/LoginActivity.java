package alex.winescanner;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
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


    }

    public void onClickLogin(View v) {
        Log.d("***DEBUG***", "Inside onClickLogin");

        String username = etUsername.getText().toString().trim();
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
        View view = getLayoutInflater().inflate(R.layout.dialog_signin, null);

        alertDialogBuilder.setView(view);

        final LinearLayout ll = view.findViewById(R.id.ll_dialog_container);

        String titleText = "Create Account";

        // Initialize a new foreground color span instance
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.WHITE);

        // Initialize a new spannable string builder instance
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

        // Apply the text color span
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        alertDialogBuilder.setCancelable(true)
            .setTitle(ssBuilder)
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {}
            })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        //override default click listener so the dialog doesn't auto close
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText etUsername = ll.findViewById(R.id.et_dialog_username);
                        final EditText etPassword = ll.findViewById(R.id.et_dialog_password);
                        final EditText etConfirmPassword = ll.findViewById(R.id.et_dialog_confirm_password);

                        String user = etUsername.getText().toString();
                        String pass = etPassword.getText().toString();
                        String confirmPass = etConfirmPassword.getText().toString();

                        //password validation
                        if(user.equals("")) {
                            Toast.makeText(ctx, "Username cannot be blank", Toast.LENGTH_SHORT).show();
                        }
                        else if(pass.equals("")) {
                            Toast.makeText(ctx, "Password cannot be blank", Toast.LENGTH_SHORT).show();
                        }
                        else if(pass.length() < 6) {
                            Toast.makeText(ctx, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        }
                        else if(!pass.equals(confirmPass)) {
                            Toast.makeText(ctx, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            createAccount(user, pass, dialogInterface);
                            Toast.makeText(ctx, "Creating Account...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(50,50,50)));

    }

    public void createAccount(String username, String password, final DialogInterface dialogInterface) {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d("***LOG***", createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(ctx, "Account Created Successfully",Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                            Intent intent = new Intent(ctx, MainActivity.class);
                            intent.putExtra("userData", user);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("***LOG***", "createUserWithEmail:failure", task.getException());
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(ctx, "Username must be in email format", Toast.LENGTH_SHORT).show();
                            }
                            else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(ctx, "That username is unavailable", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ctx, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
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
                            Intent intent = new Intent(ctx, MainActivity.class);
                            intent.putExtra("userData", user);
                            startActivity(intent);
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
