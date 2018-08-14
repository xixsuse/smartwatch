package com.tcs.sonusourav.smartwatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

/**
 * Created by SONU SOURAV on 3/29/2018.
 */

public class VerifyOTP extends AppCompatActivity {

    ActionBar VerifyOTPActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_auth);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        VerifyOTPActionBar = getSupportActionBar();
        assert VerifyOTPActionBar != null;
        VerifyOTPActionBar.setHomeButtonEnabled(true);
        VerifyOTPActionBar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);

    }
}