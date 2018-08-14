package com.tcs.sonusourav.smartwatch;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.tcs.sonusourav.smartwatch.SignInActivity.PREFS_NAME;
import static com.tcs.sonusourav.smartwatch.SignInActivity.PREF_PASSWORD;
import static com.tcs.sonusourav.smartwatch.SignInActivity.PREF_USERNAME;
import static com.tcs.sonusourav.smartwatch.SignInActivity.TAG;
import static com.tcs.sonusourav.smartwatch.SignInActivity.isLoggedIn;


public class LauncherActivity extends Activity {

    public static SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_screen);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pref = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        editor = pref.edit();
        editor.putString(isLoggedIn, "false");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

//login_in_code

        if (Objects.equals(pref.getString(isLoggedIn, null), "true")) {
            Log.d(TAG, "Launcher:onCreate: isLoggedIn=true");
            String launcherSignInUsername = pref.getString(PREF_USERNAME, null);
            String launcherSignInPassword = pref.getString(PREF_PASSWORD, null);


            assert launcherSignInUsername != null;
            assert launcherSignInPassword != null;
            (firebaseAuth.signInWithEmailAndPassword(launcherSignInUsername, launcherSignInPassword))
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Intent mainIntent = new Intent(LauncherActivity.this, FragmentSwitch.class);
                                        LauncherActivity.this.startActivity(mainIntent);
                                        LauncherActivity.this.finish();
                                    }
                                }, 1000);


                            } else {
                                Log.e("ERROR", task.getException().toString());
                                Log.d(TAG, "onCreateError: isLoggedIn=false");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Intent mainIntent = new Intent(LauncherActivity.this, SignInActivity.class);
                                        LauncherActivity.this.startActivity(mainIntent);
                                        LauncherActivity.this.finish();
                                    }
                                }, 1000);
                                Toast.makeText(LauncherActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        } else {
            /*Intent i = new Intent(LauncherActivity.this, SignInActivity.class);
            startActivity(i);*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent mainIntent = new Intent(LauncherActivity.this, SignInActivity.class);
                    LauncherActivity.this.startActivity(mainIntent);
                    LauncherActivity.this.finish();
                }
            }, 1000);

            Log.d(TAG, "onCreate: isLoggedIn=false");

        }


    }
}