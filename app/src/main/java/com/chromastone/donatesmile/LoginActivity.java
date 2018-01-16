package com.chromastone.donatesmile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import static com.chromastone.donatesmile.Constants.ALL_OK;
import static com.chromastone.donatesmile.Constants.INVALID_EMAIL;
import static com.chromastone.donatesmile.Constants.INVALID_PASSWORD;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        Email = findViewById(R.id.login_email);
        Password = findViewById(R.id.login_password);

        logIn = findViewById(R.id.login_login_button);
        SignUp = findViewById(R.id.login_sign_up_button);

        logIn.setOnClickListener(this);

        snackbar = Snackbar.make(findViewById(R.id.button3),"Invalid Credentials",Snackbar.LENGTH_INDEFINITE);
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Authenticating");
        dialog.setCancelable(false);
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
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
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
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            dialog.cancel();
                            Snackbar.make(findViewById(R.id.linearLayout2),"Authentication Failed!",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int check_email = validateEmail();
        int check_password = validatePassword();
        if(check_email == INVALID_EMAIL){
            Email.setError("Invalid email");
        }
        if(check_password == INVALID_PASSWORD){
            Password.setError("Invalid Password");
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
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
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

    private int validateEmail() {
        String email = Email.getText().toString();
        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return INVALID_EMAIL;
        else
            return ALL_OK;
    }
}
