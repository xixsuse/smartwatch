package com.tcs.sonusourav.smartwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by SONU SOURAV on 3/21/2018.
 */

public class SignInActivity extends AppCompatActivity {


    public static String PREFS_NAME = "mypref";
    public static String PREF_USERNAME = "username";
    public static String PREF_PASSWORD = "password";
    public static SharedPreferences.Editor editor;
    public static String isLoggedIn = "false";
    public static String TAG="SignInActivity";
    ProgressBar signInProgressBar;
    private SharedPreferences pref;
    private FirebaseAuth firebaseAuth;
    private EditText sign_in_username;
    private EditText sign_in_password;
    private String signInUsername;
    private String signInPassword;
    private CheckBox ch;
    private static long back_pressed;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);
        firebaseAuth = FirebaseAuth.getInstance();
        signInProgressBar = findViewById(R.id.signin_progressBar);
        sign_in_username = findViewById(R.id.ed_user_name);
        sign_in_password = findViewById(R.id.ed_password);
        Button signInButton = findViewById(R.id.button_sign_in);
        TextView signUpTextView = findViewById(R.id.sign_up_tv);
        TextView forgotPasswordTextView = findViewById(R.id.tv_forgot_password);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        pref = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        editor = pref.edit();
        editor.putString(isLoggedIn, "false");


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    signInUsername = sign_in_username.getText().toString().trim();
                    signInPassword = sign_in_password.getText().toString().trim();


                if ((signInUsername == null) || signInUsername.equals("") || TextUtils.isEmpty(signInUsername) || (signInUsername.length() == 0)) {
                    Toast.makeText(getApplicationContext(), "Enter your username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (signInPassword.equals("") || (signInPassword == null) || TextUtils.isEmpty(signInPassword) || signInPassword.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }


                ch = findViewById(R.id.remember_me_chk_box);


                signInProgressBar.setVisibility(View.VISIBLE);


                (firebaseAuth.signInWithEmailAndPassword(signInUsername, signInPassword))
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    if (ch.isChecked()) {
                                        editor.putString(isLoggedIn, "true");
                                        editor.putString(PREF_USERNAME, signInUsername);
                                        editor.putString(PREF_PASSWORD, signInPassword);
                                        editor.apply();

                                    }

                                    signInProgressBar.setVisibility(View.GONE);

                                    Intent i = new Intent(SignInActivity.this, FragmentSwitch.class);
                                    // i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                                    startActivity(i);
                                    sign_in_username.setText("");
                                    sign_in_password.setText("");
                                    ch.setChecked(false);
                                } else {
                                    Log.e("ERROR", task.getException().toString());
                                    signInProgressBar.setVisibility(View.GONE);

                                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


            }
        });


        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);

                sign_in_password.setText("");
                sign_in_password.setText("");
            }
        });


        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignInActivity.this, ResetPassword.class);
                startActivity(intent);
            }
        });


        sign_in_password.setText("");
        sign_in_password.setText("");
    }

    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
        } else
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

}
