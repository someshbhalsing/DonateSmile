package com.chromastone.donatesmile;


import android.content.Intent;
import android.os.Bundle;
import com.chromastonetech.librarymodule.SigninListener;

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
