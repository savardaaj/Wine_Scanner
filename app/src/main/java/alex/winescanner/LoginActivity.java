package alex.winescanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Context ctx;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private final int RC_SIGN_IN = 1;
    private final int FB_SIGN_IN = 2;

    CallbackManager callbackManager;

    EditText etUsername;
    EditText etPassword;
    CheckBox cbSaveLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();

        try {
            ctx = this;

            etUsername = findViewById(R.id.etUsername);
            etPassword = findViewById(R.id.etPassword);
            cbSaveLogin = findViewById(R.id.cb_remember_login);

            mAuth = FirebaseAuth.getInstance();

            loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

            saveLogin = loginPreferences.getBoolean("rememberMe", false);
            if (saveLogin) {

                etUsername.setText(loginPreferences.getString("username", ""));
                etPassword.setText(loginPreferences.getString("password", ""));
                cbSaveLogin.setChecked(true);
                Log.d("***DEBUG***", "loginPrefsUser " + loginPreferences.getString("username", "") );
            }
        }
        catch (Exception e) {

        }

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
            signInDefaultCredentials(username, password);
        }

    }

    public void onClickGoogle(View v) {
        Log.d("***DEBUG***", "Inside onClickGoogle");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onClickFacebook(View v) {
        Log.d("***DEBUG***", "Inside onClickFacebook");

    // Callback registration
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Log.d("***DEBUG***", "Inside onClickFacebook success");
                }

                @Override
                public void onCancel() {
                    // App code
                    Log.d("***DEBUG***", "Inside onClickFacebook cancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.d("***DEBUG***", "Inside onClickFacebook error");
                }
            });
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
                            createDefaultAccount(user, pass, dialogInterface);
                            Toast.makeText(ctx, "Creating Account...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(50,50,50)));

    }

    public void createDefaultAccount(String username, String password, final DialogInterface dialogInterface) {
        Log.d("***DEBUG***", "inside createDefaultAccount");
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(ctx, "Account Created Successfully",Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                            Intent intent = new Intent(ctx, LibraryActivity.class);
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

    public void signInDefaultCredentials(final String username, final String password) {
        Log.d("***DEBUG***", "inside signInDefaultCredentials");
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("***LOG***", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(cbSaveLogin.isChecked()) {
                                storeCredentials(username, password);
                            }
                            else {
                                removeCredentials();
                            }
                            startLibraryActivity();
                        }
                        else if(task.getException() instanceof FirebaseAuthInvalidUserException) {
                            Log.w("***LOG***", "signInWithEmail:failure", task.getException());
                            Toast.makeText(ctx, "Account does not exist",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.w("***LOG***", "signInWithEmail:failure", task.getException());
                            Toast.makeText(ctx, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("***DEBUG***", "inside firebaseAuthWithGoogle" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("***DEBUG***", "signInWithCredential:success");
                            startLibraryActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("***DEBUG***", "signInWithCredential:failure", task.getException());
                            Toast.makeText(ctx, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("***DEBUG***", "Google sign in failed", e);
                // ...
            }
        }
        else{
            Log.d("***DEBUG***", "Inside onClickFacebook result");
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            startLibraryActivity();
        }
    }

    public void storeCredentials(String username, String password) {

        try {
            //SharedPreferences.Editor editor = new SecurePreferences(this, password, "my_user_prefs.xml").edit();
            loginPrefsEditor = loginPreferences.edit();
            loginPrefsEditor.putString("username", username);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.putBoolean("rememberMe", true);

            loginPrefsEditor.apply();

        }
        catch(Exception e) {

        }
    }

    public void removeCredentials() {
        Log.d("***DEBUG***", "inside removeCredentials");

        loginPrefsEditor = loginPreferences.edit();
        loginPrefsEditor.clear();
        loginPrefsEditor.apply();
    }

    public void startLibraryActivity() {
        Log.d("***DEBUG***", "inside startLibraryActivity");

        FirebaseUser user = mAuth.getCurrentUser();
        //proceed to LibraryActivity new intent
        Intent intent = new Intent(ctx, LibraryActivity.class);
        intent.putExtra("userData", user);
        startActivity(intent);
    }

}
