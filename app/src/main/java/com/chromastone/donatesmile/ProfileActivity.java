package com.chromastone.donatesmile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText phone;
    private Button verifyEmail;
    private Button verifyPhone;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog dialog;
    private ImageView EmailVerified;
    private ImageView PhoneVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        phone = findViewById(R.id.profile_phone);

        verifyEmail = findViewById(R.id.profile_email_verify);
        verifyPhone = findViewById(R.id.profile_phone_verify);

        EmailVerified = findViewById(R.id.profile_email_verified);
        PhoneVerified = findViewById(R.id.profile_phone_verified);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        new ProfileActivity.BackGroundClass().execute("setHeader");

        verifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.validateEmail(email.getText().toString())==Constants.ALL_OK){
                    dialog.show();
                    Constants.sendVerificationEmail(new OnEmailVerificationSent() {
                        @Override
                        public void emailSent() {
                            Snackbar.make(findViewById(R.id.profile_submit),"Verification Email Sent Successfully!",Snackbar.LENGTH_LONG).show();
                            dialog.cancel();
                        }

                        @Override
                        public void emailSendingFailed() {
                            dialog.cancel();
                            Snackbar.make(findViewById(R.id.profile_submit),"Failed to send verification email",Snackbar.LENGTH_LONG).show();
                        }
                    });
                }else{
                    email.setError("Invalid Email");
                }
            }
        });
        initProgressDialog("Sending verification link");
    }

    public class BackGroundClass extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String string1 = strings[0];
            if (string1.contentEquals("setHeader")){
                if (mUser.getDisplayName() != null){
                    name.setText(mUser.getDisplayName());
                }
                if (mUser.getEmail() != null){
                    email.setText(mUser.getEmail());
                }
                if(mUser.isEmailVerified()){
                    verifyEmail.setVisibility(View.GONE);
                    EmailVerified.setVisibility(View.VISIBLE);

                }
            }
            return "Success";
        }
    }

    void initProgressDialog(String Message){
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(Message);
        dialog.setCancelable(false);
    }

}