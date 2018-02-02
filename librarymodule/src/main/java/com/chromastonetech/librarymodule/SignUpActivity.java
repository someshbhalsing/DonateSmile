package com.chromastonetech.librarymodule;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.chromastonetech.librarymodule.Constants.ALL_OK;
import static com.chromastonetech.librarymodule.Constants.INVALID_EMAIL;
import static com.chromastonetech.librarymodule.Constants.INVALID_PASSWORD;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText Email;
    private EditText Password;
    private EditText Re_Password;
    private Button Submit;
    private CheckBox box;
    private TextView TNC;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Email = findViewById(R.id.signup_email);
        Password = findViewById(R.id.signup_password);
        Re_Password = findViewById(R.id.signup_re_pass);
        Submit = findViewById(R.id.signup_button);
        box = findViewById(R.id.signup_checkbox);
        TNC = findViewById(R.id.signup_tnc);

        Submit.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Creating account");
        dialog.setCancelable(false);

    }

    @Override
    public void onClick(View v) {
        if(Constants.validateEmail(Email.getText().toString()) == INVALID_EMAIL){
            Email.setError("Invalid Email");
            return;
        }
        if (validatePassword(Password) == INVALID_PASSWORD){
            Password.setError("Invalid Password");
            return;
        }
        if(validatePassword(Re_Password) == INVALID_PASSWORD){
            Re_Password.setError("Invalid Password");
            return;
        }
        if (!box.isChecked()){
            Snackbar.make(findViewById(R.id.signup_button),"Please accept the terms and conditions",Snackbar.LENGTH_LONG).show();
            return;
        }
        else{
            dialog.show();
            mAuth.createUserWithEmailAndPassword(Email.getText().toString(),Password.getText().toString())
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mAuth.signOut();
                                finish();
                            }else{
                                dialog.cancel();
                                Snackbar.make(findViewById(R.id.signup_button),"Account creation failed :(",Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private int validatePassword(EditText Password) {
        String password = Password.getText().toString();
        if(TextUtils.isEmpty(password) || password.length()<6)
            return INVALID_PASSWORD;
        return ALL_OK;
    }
}