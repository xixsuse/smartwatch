package com.tcs.sonusourav.smartwatch;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HistorySwitcher extends AppCompatActivity {

    Fragment fragment ;
    private Calendar myCalendar;
    private TextView details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_frame_layout);

        details = findViewById(R.id.hb_date_picker);
        myCalendar = Calendar.getInstance();
        final FragmentManager hisFragmentManager = getSupportFragmentManager();
        Spinner historySpinner = findViewById(R.id.history_spinner);


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item2, getResources().getStringArray(R.array.history)
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item2);
        historySpinner.setAdapter(spinnerArrayAdapter);
        Log.d("b4onItemSelected","reaching");


        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = hisFragmentManager.beginTransaction();
        transaction.replace(R.id.history_frame, HeartbeatHistory.newInstance());
        transaction.commit();


        historySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        Log.d("onItemSelected","reaching");

                        switch (position) {
                            case 0:
                                fragment = HeartbeatHistory.newInstance(null);
                                break;
                            case 1:
                                fragment = BloodpressureHistory.newInstance(null);
                                break;
                            default:
                                fragment=HeartbeatHistory.newInstance(null);

                        }

                        FragmentTransaction transaction = hisFragmentManager.beginTransaction();
                        transaction.replace(R.id.history_frame, fragment).commit();


                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        fragment = BloodpressureHistory.newInstance(null);
                        FragmentTransaction transaction = hisFragmentManager.beginTransaction();
                        transaction.replace(R.id.history_frame, fragment).commit();

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
        details.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(HistorySwitcher.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



    }


    //updateLabel Method
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        details.setText(sdf.format(myCalendar.getTime()));
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar HistorySwitcherActionBar = getSupportActionBar();
        assert HistorySwitcherActionBar != null;

        HistorySwitcherActionBar.setHomeButtonEnabled(true);
        HistorySwitcherActionBar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(HistorySwitcher.this,FragmentSwitch.class));
            return true;
        } else
            return false;
    }




    }
