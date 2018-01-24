package com.chromastone.donatesmile;

import android.app.ProgressDialog;
import android.net.Uri;
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
import android.widget.LinearLayout;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText otp;

    private Button verifyEmail;
    private Button verifyPhone;
    private Button resendOTP;
    private Button submitOTP;
    private Button Submit;

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

    private LinearLayout ll1;

    private android.support.v7.app.AlertDialog.Builder alertDialog;

    private boolean otpSent;
    private com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private Uri ProfilePictureURI;
    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        phone = findViewById(R.id.profile_phone);
        otp = findViewById(R.id.profile_phone_otp);

        profilePic = findViewById(R.id.profile_profile_image);
        profileEdit = findViewById(R.id.profile_image_edit);
        profilePicProgress = findViewById(R.id.profile_image_progress);

        verifyEmail = findViewById(R.id.profile_email_verify);
        verifyPhone = findViewById(R.id.profile_phone_verify);
        resendOTP = findViewById(R.id.profile_phone_resend);
        submitOTP = findViewById(R.id.profile_otp_submit);
        Submit = findViewById(R.id.profile_submit);

        ll1 = findViewById(R.id.profile_otp_layout);

        EmailVerified = findViewById(R.id.profile_email_verified);
        PhoneVerified = findViewById(R.id.profile_phone_verified);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        displayName = mUser.getDisplayName();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                hideViews(ll1);
                hideViews(verifyPhone);
                unhideView(PhoneVerified);
                dialog.cancel();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("PhoneVerificationFailed",e.toString());
                dialog.cancel();
                Snackbar.make(findViewById(R.id.profile_submit),"Phone verification failed!",Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                unhideView(ll1);
                hideViews(verifyPhone);
                otpSent = true;
                mVerificationId = s;
                mToken = forceResendingToken;
                dialog.cancel();
            }

        };
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()){
            if (user.getProviderId().equals("google.com")){
                name.setEnabled(false);
                Log.d("ABC","GOOGLE");
                hideViews(profileEdit);
                if (mUser.getPhotoUrl() != null) {
                    Picasso.with(ProfileActivity.this).load(mUser.getPhotoUrl()).into(profilePic, new Callback() {
                        @Override
                        public void onSuccess() {
                            hideViews(profilePicProgress);
                        }

                        @Override
                        public void onError() {
                            hideViews(profilePicProgress);
                        }
                    });
                }else{
                    hideViews(profilePicProgress);
                }
            }else if (user.getProviderId().equals("phone")&&user.getPhoneNumber()!=null){
                unhideView(PhoneVerified);
                hideViews(verifyPhone);
                phone.setEnabled(false);
                phone.setText(mUser.getPhoneNumber());
            }else{
                hideViews(profilePicProgress);
            }
        }

        new ProfileActivity.BackGroundClass().execute("setHeader");

        verifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProgressDialog("Sending verification link");
                if (Constants.validateEmail(email.getText().toString())==Constants.ALL_OK){
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
                    startPhoneAuthentication("+91"+phone.getText().toString());
                    initProgressDialog("Sending OTP");
                    return;
                }
                phone.setError("Invalid mobile no");
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.validatePhone(phone.getText().toString()) == Constants.ALL_OK) {
                    resendVerificationCode("+91"+phone.getText().toString(), mToken);
                    initProgressDialog("Sending OTP");
                    return;
                }
                phone.setError("");
            }
        });

        submitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString() != null && otp.getText().toString().length() == 6){
                    dialog.cancel();
                    verifyPhoneNumberWithCode(mVerificationId,otp.getText().toString());
                    return;
                }
                otp.setError("Invalid OTP");
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        boolean edited = false;
        if (!displayName.equals(name.getText().toString())){
            builder.setDisplayName(name.getText().toString());
            edited = true;
        }
        if (ProfilePictureURI != null){
            builder.setPhotoUri(ProfilePictureURI);
            edited = true;
        }
        if (edited){
            mUser.updateProfile(builder.build());
        }
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
                    hideViews(verifyEmail);
                    unhideView(EmailVerified);
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
        dialog.show();
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
                                    hideViews(ll1);
                                    hideViews(verifyPhone);
                                    unhideView(PhoneVerified);
                                    phone.setEnabled(false);
                                    Toast.makeText(ProfileActivity.this, "Phone authenticated!", Toast.LENGTH_SHORT).show();
                                }else{
                                    otp.setError("Incorrect OTP");
                                }
                            }
                        });
    }

    private void hideViews(View... views){
        for (View view : views){
            view.setVisibility(View.GONE);
        }
    }

    private void unhideView(View... views){
        for (View view : views){
            view.setVisibility(View.VISIBLE);
        }
    }
}
