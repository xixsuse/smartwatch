package com.tcs.sonusourav.smartwatch;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SONU SOURAV on 3/27/2018.
 */

public class SignUpActivity extends AppCompatActivity {

    Calendar myCalendar;
    String sign_up_email;
    String sign_up_username;
    String sign_up_dob;
    String sign_up_gender;
    String sign_up_phone;
    String sign_up_password;
    Intent i;
    private String sign_up_phonecode;
    private String sign_up_phone_with_code;
    private EditText inputEmail, inputPassword, inputName, inputDOB, inputPhone;
    private Spinner inputGender;
    private CheckBox inputCheck;
    private CountryCodePicker inputPhoneCode;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseInstance;
    private DatabaseReference rootRef;
    private DatabaseReference userIdRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        inputName = findViewById(R.id.et1);
        inputEmail = findViewById(R.id.et2);
        inputDOB = findViewById(R.id.et3);
        inputGender = findViewById(R.id.spin_gender);
        inputPhoneCode = findViewById(R.id.sign_up_ccp);
        inputPhone = findViewById(R.id.et4);
        inputPassword = findViewById(R.id.et5);
        inputCheck = findViewById(R.id.cb1);
        progressBar = findViewById(R.id.signup_progressBar);
        btnSignUp = findViewById(R.id.button_sign_up);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item1, getResources().getStringArray(R.array.gender)
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item1);
        inputGender.setAdapter(spinnerArrayAdapter);
        myCalendar = Calendar.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseInstance = FirebaseDatabase.getInstance();
        rootRef = firebaseInstance.getReference("users");
        userIdRef = firebaseInstance.getReference("UserId");




        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sign_up_email = inputEmail.getText().toString().trim();
                sign_up_username = inputName.getText().toString().trim();
                sign_up_dob = inputDOB.getText().toString().trim();
                sign_up_gender = inputGender.getSelectedItem().toString();
                sign_up_phonecode = inputPhoneCode.getSelectedCountryCodeWithPlus();
                sign_up_phone = inputPhone.getText().toString().trim();
                sign_up_password = inputPassword.getText().toString().trim();
                sign_up_phone_with_code = (sign_up_phonecode + sign_up_phone);
                if (TextUtils.isEmpty(sign_up_username) || (sign_up_username.equals("")) || (sign_up_username == null) || (sign_up_username.length() == 0)) {
                    Toast.makeText(getApplicationContext(), "Enter your kind name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sign_up_email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(sign_up_dob)) {
                    Toast.makeText(getApplicationContext(), "Enter your Date of birth!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int actualPositionOfMySpinner = inputGender.getSelectedItemPosition();
                String selectedItemOfMySpinner = (String) inputGender.getItemAtPosition(actualPositionOfMySpinner);

                if (selectedItemOfMySpinner.isEmpty() || selectedItemOfMySpinner.equalsIgnoreCase("sign_up_gender")) {
                    Toast.makeText(getApplicationContext(), "Select your sign_up_gender", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (TextUtils.isEmpty(sign_up_phone)) {
                    Toast.makeText(getApplicationContext(), "Enter your mobile number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sign_up_phone.length() < 10) {
                    Toast.makeText(getApplicationContext(), "Mobile number should contain ten digits.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sign_up_password)) {
                    Toast.makeText(getApplicationContext(), "Enter sign_up_password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sign_up_password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!validateEmail(sign_up_email)) {
                    Toast.makeText(getApplicationContext(), "Invalid Email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!inputCheck.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please agree to the terms and conditons", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);
                mobileNumberExistenceCheck(sign_up_phone_with_code);


            }
        });


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        inputDOB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SignUpActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }




    //updateLabel Method
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        inputDOB.setText(sdf.format(myCalendar.getTime()));
    }


    //validateEmailMethod
    public boolean validateEmail(String email) {

        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }


    public void mobileNumberExistenceCheck(final String mobileNumber) {


        userIdRef.child(mobileNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Mobile number already exists", Toast.LENGTH_SHORT).show();

                } else {


                    progressBar.setVisibility(View.GONE);
                    i = new Intent(SignUpActivity.this, MyPhoneAuth.class);
                    i.putExtra("PhoneNumber", mobileNumber);
                    i.putExtra("Email", sign_up_email);
                    i.putExtra("Name", sign_up_username);
                    i.putExtra("DOB", sign_up_dob);
                    i.putExtra("Gender", sign_up_gender);
                    i.putExtra("Password", sign_up_password);
                    i.putExtra("Mobile", sign_up_phone);

                    startActivity(i);
                    Log.d("phone no.",mobileNumber);
                    Log.d("Email",sign_up_email);
                    Log.d("Name",sign_up_username);
                    Log.d("DOB",sign_up_dob);
                    Log.d("Gender",sign_up_gender);
                    Log.d("Password",sign_up_password);
                    Log.d("Mobile",sign_up_phone);


                    inputName.setText("");
                    inputEmail.setText("");
                    inputDOB.setText("");
                    inputPhone.setText("");
                    inputPassword.setText("");
                    inputGender.setSelection(0);
                    inputCheck.setChecked(false);
                    inputName.requestFocus();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}