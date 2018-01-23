package com.chromastone.donatesmile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

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

    private ImageView profilePic;
    private ImageView profileEdit;
    private ProgressBar profilePicProgress;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mToken;

    private android.support.v7.app.AlertDialog.Builder alertDialog;

    private boolean otpSent;
    private com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        phone = findViewById(R.id.profile_phone);

        profilePic = findViewById(R.id.profile_profile_image);
        profileEdit = findViewById(R.id.profile_image_edit);
        profilePicProgress = findViewById(R.id.profile_image_progress);

        verifyEmail = findViewById(R.id.profile_email_verify);
        verifyPhone = findViewById(R.id.profile_phone_verify);

        EmailVerified = findViewById(R.id.profile_email_verified);
        PhoneVerified = findViewById(R.id.profile_phone_verified);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                otpSent = true;
                mVerificationId = s;
                mToken = forceResendingToken;
            }
        };
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()){
            if (user.getProviderId().equals("google.com")){
                name.setEnabled(false);
                Log.d("ABC","GOOGLE");
                profileEdit.setVisibility(View.GONE);
                if (mUser.getPhotoUrl() != null) {
                    Picasso.with(ProfileActivity.this).load(mUser.getPhotoUrl()).into(profilePic, new Callback() {
                        @Override
                        public void onSuccess() {
                            profilePicProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            profilePicProgress.setVisibility(View.GONE);
                        }
                    });
                }
            }else if (user.getProviderId().equals("phone")){
                PhoneVerified.setVisibility(View.VISIBLE);
                verifyPhone.setVisibility(View.GONE);
                phone.setEnabled(false);
                phone.setText(mUser.getPhoneNumber());
            }
        }
        /*if (mUser.getProviderId().contentEquals("google.com")) {

        }else{
            profilePicProgress.setVisibility(View.GONE);
        }*/
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

        verifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpSent = false;
                if (Constants.validatePhone(phone.getText().toString()) == Constants.ALL_OK){
                    startPhoneAuthentication(phone.getText().toString());
                    initAlertDialog();
                    alertDialog.show();
                }
            }
        });
        initProgressDialog("Sending verification link");
    }

    private void startPhoneAuthentication(String s) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(s, 2, TimeUnit.MINUTES, ProfileActivity.this, mCallbacks);
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

    void initAlertDialog(){
        alertDialog = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this);
        alertDialog.setView(R.layout.phone_auth);
        final EditText otpField = findViewById(R.id.phone_otp);
        alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String OTP = otpField.getText().toString();
                if (otpSent && OTP.length() == 6){
                    verifyPhoneNumberWithCode(mVerificationId,OTP);
                    dialog.cancel();
                }else{
                    otpField.setError("Invalid OTP");
                }
            }
        });
        alertDialog.setNeutralButton("Resend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resendVerificationCode(phone.getText().toString(),mToken);
            }
        });
        alertDialog.setCancelable(false);

    }

    private void cancelAlertDialog(){


    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mUser.updatePhoneNumber(credential)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Phone authenticated!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ProfileActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
/*
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(ProfileActivity.this, "phone authenticated", Toast.LENGTH_SHORT).show();
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                Toast.makeText(ProfileActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // [END_EXCLUDE]
                        }
                    }
                });*/
    }

}
