package com.tcs.sonusourav.smartwatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.shinelw.library.ColorArcProgressBar;

/**
 * Created by SONU SOURAV on 4/8/2018.
 */

public class HeartRateAnalysis extends AppCompatActivity {

    private ActionBar heartRateActionBar;
    private Button hrCancelButton;
    private ColorArcProgressBar hbAnalysisBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heartbeat_analysis);

        hrCancelButton = findViewById(R.id.heartbeat_cancel_btn);
        hbAnalysisBar=findViewById(R.id.hb_progress_bar);
        hrCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HeartRateAnalysis.this,HeartbeatConnect.class));
                System.exit(0);
                // finish notworking
            }
        });


        hbAnalysisBar.setCurrentValues(100);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(HeartRateAnalysis.this,HeartRateResult.class));
                finish();

            }
        }, 1600);
    }

    public void onResume() {
        super.onResume();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Heart rate");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        heartRateActionBar = getSupportActionBar();

        heartRateActionBar.setTitle("Heart rate");

        heartRateActionBar.setHomeButtonEnabled(true);
        heartRateActionBar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
