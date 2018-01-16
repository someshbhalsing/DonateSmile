package com.chromastone.donatesmile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private EditText Re_Password;
    private Button Submit;
    private CheckBox box;
    private TextView TNC;

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
    }
}
