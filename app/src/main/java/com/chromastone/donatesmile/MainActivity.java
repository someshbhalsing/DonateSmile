package com.chromastone.donatesmile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    String cityToBePassed;

    private int year;
    private int month;
    private int day;
EditText email,name,no,lastname;

    static final int DATE_PICKER_ID = 1111;


    Button submit;

    TextView changedate;
    String[]array={"AB+","O-","A+","A-","B-","B+","AB-","O+"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Toast.makeText(this, "Signed in as "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
        }

        submit=(Button)findViewById(R.id.submit);

        Spinner getmessage = (Spinner) findViewById(R.id.getmsg);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, array);

        email=(EditText)findViewById(R.id.email);
        name=(EditText)findViewById(R.id.doname);
        no=(EditText)findViewById(R.id.no);
        lastname=(EditText)findViewById(R.id.name);


        getmessage.setAdapter(adapter);
        getmessage.setOnItemSelectedListener(this);
        changedate=(TextView)findViewById(R.id.date);

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String itemname = name.getText().toString();


                String itemID = "" + itemname;

                String itemlastname = lastname.getText().toString();


                String itemIDs = "" + itemlastname;

                    String MobilePattern = "[0-9]{10}";
                    String emaill = email.getText().toString().trim();
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

//                if (name.length() > 25) {
//
//                    Toast.makeText(getApplicationContext(), "pls enter less the 25 character in user name", Toast.LENGTH_SHORT).show();

                if (TextUtils.isEmpty(itemname)) {
                    //name is empty
                    Toast.makeText(MainActivity.this, "Please enter Valid name ", Toast.LENGTH_SHORT).show();
                    // stopping the function execution further

                    return;
                }



               else if (TextUtils.isEmpty(itemlastname)) {
                    //email is empty
                    Toast.makeText(MainActivity.this, "Please enter Last Name ", Toast.LENGTH_SHORT).show();
                    // stopping the function execution further

                    return;
                }
//                }

                else if (name.length() == 0 || lastname.length() == 0 || no.length() ==
                            0 || email.length() == 0) {

                        Toast.makeText(getApplicationContext(), "pls fill the empty fields", Toast.LENGTH_SHORT).show();


                    }


                    else if (!email.getText().toString().matches(emailPattern)) {

                        Toast.makeText(getApplicationContext(), "Please Enter Valid Email Address", Toast.LENGTH_SHORT).show();


                    } else if (!no.getText().toString().matches(MobilePattern)) {

                        Toast.makeText(getApplicationContext(), "Please enter valid 10 digit phone number", Toast.LENGTH_SHORT).show();

                    }

            }
        });


        changedate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append("Select Date :").append(" ").append("").append(" ")
                .append(" ").append(" "));

        changedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                showDialog(DATE_PICKER_ID);


            }
        });
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date
          changedate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

        }
    };


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        cityToBePassed=parent.getItemAtPosition(position).toString();

//        Toast.makeText(MainActivity.this, " select blood group", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
