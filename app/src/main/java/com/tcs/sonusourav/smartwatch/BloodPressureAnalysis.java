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
import android.widget.ImageButton;

import com.shinelw.library.ColorArcProgressBar;

/**
 * Created by SONU SOURAV on 4/8/2018.
 */

public class BloodPressureAnalysis extends AppCompatActivity {

    private ActionBar bloodPressureActionBar;
    private Button bpCancelButton;
    private ColorArcProgressBar bpAnalysisBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloodpressure_analysis);

        bpCancelButton = findViewById(R.id.bloodpressue_cancel_button);
        bpAnalysisBar=findViewById(R.id.bp_progress_bar);

        bpCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BloodPressureAnalysis.this,BloodpressureConnect.class));
                System.exit(0);
                // finish notworking
            }
        });

        bpAnalysisBar.setCurrentValues(100);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(BloodPressureAnalysis.this,BloodPressureResult.class));
                finish();

            }
        }, 1600);
    }

    public void onResume() {
        super.onResume();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Blood pressure");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        bloodPressureActionBar = getSupportActionBar();

        bloodPressureActionBar.setTitle("Blood pressure");

        bloodPressureActionBar.setHomeButtonEnabled(true);
        bloodPressureActionBar.setDisplayHomeAsUpEnabled(true);
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
