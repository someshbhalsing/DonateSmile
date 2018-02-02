package com.chromastonetech.donatesmilengo;

import android.content.Intent;
import android.os.Bundle;

import com.chromastonetech.librarymodule.SigninListener;

/**
 * Created by somesh on 02/02/2018.
 */

public class LoginActivity extends com.chromastonetech.librarymodule.LoginActivity implements SigninListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSignInListener(this);
    }

    @Override
    public void onSignInSuccessful() {
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
    }
}
