package com.chromastonetech.librarymodule;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.chromastonetech.librarymodule.Constants.ALL_OK;
import static com.chromastonetech.librarymodule.Constants.INVALID_EMAIL;
import static com.chromastonetech.librarymodule.Constants.INVALID_PASSWORD;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    protected EditText Email;
    protected EditText Password;

    private Button logIn;
    private Button SignUp;

    private Snackbar snackbar;
    private ProgressDialog dialog;
    private TextView ForgotPassword;
    private static final int REQUEST_CODE = 1001;
    private SigninListener signInListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("968015308280-510i94g6cho0qdbjk5vkqtibgqackfl5.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        ForgotPassword = findViewById(R.id.login_forgot_password);
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Password Recovery");
                builder.setView(R.layout.forgot_password);
                final EditText email = findViewById(R.id.forgot_email);
                builder.setMessage("Please enter registered email address");
                builder.setPositiveButton("Send reset link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        forgotPassword(email.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });
        Email = findViewById(R.id.login_email);
        Password = findViewById(R.id.login_password);

        logIn = findViewById(R.id.login_login_button);
        SignUp = findViewById(R.id.login_sign_up_button);

        logIn.setOnClickListener(this);

        snackbar = Snackbar.make(findViewById(R.id.button3),"Invalid Credentials",Snackbar.LENGTH_LONG);
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Authenticating");
        dialog.setCancelable(false);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LoginActivity.this,SignUpActivity.class),REQUEST_CODE);
            }
        });
    }

    public void signInWithGoogle(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        dialog.show();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("LOGIN", "onConnectionFailed:" + connectionResult);
        dialog.cancel();
        Snackbar.make(findViewById(R.id.linearLayout2),"Please update to the latest version of google play services",Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onStart() {
        super.onStart();
        //CHECK IF SOME USER IS ALREADY LOGGED IN
        if (mAuth.getCurrentUser() != null) {
            signInListener.onSignInSuccessful();
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                dialog.cancel();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                dialog.cancel();
                Log.d("Login","failed");
                Snackbar.make(findViewById(R.id.linearLayout2),"Google SignIn Failed",Snackbar.LENGTH_SHORT).show();
            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.cancel();
                            Log.d("Login","Success");
                            try {
                                Snackbar.make(findViewById(R.id.linearLayout2), "Signed In as : " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Snackbar.LENGTH_SHORT).show();
                            }catch (NullPointerException e){
                                Log.d("ABC",e.toString());
                            }
                            signInListener.onSignInSuccessful();
                            //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            dialog.cancel();
                            Snackbar.make(findViewById(R.id.linearLayout2),"Authentication Failed!",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        dialog.show();
        int check_email = Constants.validateEmail(Email.getText().toString());
        int check_password = validatePassword();
        if(check_email == INVALID_EMAIL){
            Email.setError("Invalid email");
            dialog.cancel();
            return;
        }
        if(check_password == INVALID_PASSWORD){
            Password.setError("Invalid Password");
            dialog.cancel();
            return;
        }
        if (check_email == ALL_OK && check_password == ALL_OK){
            mAuth.signInWithEmailAndPassword(Email.getText().toString(),Password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                dialog.cancel();
                                Log.d("EmailLogin","Successful for user "+Email.getText().toString());
                                signInListener.onSignInSuccessful();
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }else{
                                dialog.cancel();
                                snackbar.show();
                            }
                        }
                    });
        }
    }

    private int validatePassword() {
        String password = Password.getText().toString();
        if(TextUtils.isEmpty(password) || password.length()<6)
            return INVALID_PASSWORD;
        return ALL_OK;
    }

    private void forgotPassword(String email){
        dialog.setMessage("Sending");
        dialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.cancel();
                            Snackbar.make(findViewById(R.id.button3),"Password reset email sent successfully!",Snackbar.LENGTH_LONG).show();
                        }else{
                            dialog.cancel();
                            Snackbar.make(findViewById(R.id.button3),"Link sending failed :(",Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /*
    * */

    protected void setSignInListener(SigninListener signInListener){
        this.signInListener = signInListener;
    }
}