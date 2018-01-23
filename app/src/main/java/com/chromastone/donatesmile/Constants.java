package com.chromastone.donatesmile;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by somesh on 16/01/2018.
 */

public class Constants {

    public static final int ALL_OK = 0;
    public static final int INVALID_EMAIL = 1;
    public static final int INVALID_PASSWORD = 2;
    public static final int VERIFICATION_LINK_SENT = 1002;

    public static int validateEmail(String email) {
        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return INVALID_EMAIL;
        else
            return ALL_OK;
    }

    public static void sendVerificationEmail(final OnEmailVerificationSent onEmailVerificationSent) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                onEmailVerificationSent.emailSent();
                            }
                            else{
                                onEmailVerificationSent.emailSendingFailed();
                            }
                        }
                    });
        }
    }
}
