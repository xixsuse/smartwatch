package com.tcs.sonusourav.smartwatch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by SONU SOURAV on 3/18/2018.
 */

public class ResetPassword extends AppCompatActivity {

    ActionBar ResetPassActionBar;
    EditText resetEmail;
    Button resetPasswordButton;
    ProgressBar resetProgressBar;
    String email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);

        auth = FirebaseAuth.getInstance();
        resetEmail = findViewById(R.id.email_forgot_pass);
        resetPasswordButton = findViewById(R.id.button_reset_password);
        resetProgressBar = findViewById(R.id.reset_progress_bar);


        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
                //startActivity(new Intent(SignupActivity1.this, ResetPasswordActivity.class));
            }
        });

    }

    public void resetPassword() {

        String email = resetEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
            return;
        }


        resetProgressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            resetProgressBar.setVisibility(View.GONE);
                            Toast.makeText(ResetPassword.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();

                            finish();



                        } else {
                            Toast.makeText(ResetPassword.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        resetProgressBar.setVisibility(View.GONE);
                        finish();
                    }
                });


    }


    protected void onResume() {
        super.onResume();
        resetProgressBar.setVisibility(View.GONE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        ResetPassActionBar = getSupportActionBar();
        assert ResetPassActionBar != null;

        ResetPassActionBar.setHomeButtonEnabled(true);
        ResetPassActionBar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else
            return false;
    }



}
