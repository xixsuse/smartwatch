package com.tcs.sonusourav.smartwatch;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HeartbeatHistory extends android.support.v4.app.Fragment {

    ListView hblistView;
    ArrayList<HeartbeatHistoryList> hbList;
    private String TAG="HeartbeatHistory";
    private LineChart hbChart;
    private DatabaseReference hbHistory;
    private DatabaseReference hbEmailRef;
    String testEmail;
    String date;
    int i=0;
    List<Entry> value=new ArrayList<>();
    long startDate;
    static String encodeUserEmail(String userEmail) { return userEmail.replace(".", ",");
    }
    public static HeartbeatHistory newInstance() {
        return new HeartbeatHistory();
    }

    public static android.support.v4.app.Fragment newInstance(Bundle savedInstanceState) {

        android.support.v4.app.Fragment fragment = new HeartbeatHistory();
        if (savedInstanceState != null) {
            fragment.setArguments(savedInstanceState);
        }
        return fragment;
    }


    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View heartbeatView = inflater.inflate(R.layout.history_heartbeat, container, false);
        hbChart = heartbeatView.findViewById(R.id.heartbeat_linechart);
        hblistView = heartbeatView.findViewById(R.id.heartbeat_listview);
        hbList = new ArrayList<>();


        FirebaseAuth ecAuth = FirebaseAuth.getInstance();
        FirebaseDatabase hbInstance = FirebaseDatabase.getInstance();
        DatabaseReference hbRootRef = hbInstance.getReference("Users");
        FirebaseUser hbuser = ecAuth.getCurrentUser();
        assert hbuser != null;
        testEmail = encodeUserEmail(Objects.requireNonNull(hbuser.getEmail()));
        hbEmailRef = hbRootRef.child(testEmail).getRef();
        hbHistory = hbEmailRef.child("HeartrateHistory").getRef();


        final HeartbeatAdapterM hbCustomAdapter = new HeartbeatAdapterM(getActivity(), hbList);
        hblistView.setAdapter(hbCustomAdapter);



        hbHistory.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    DatabaseReference keyReference = hbHistory.getRef().child(key).getRef();

                    keyReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot childDataSnapshot) {
                            String date = childDataSnapshot.child("HBhistoryDate").getValue(String.class);
                            String time = childDataSnapshot.child("HBhistoryTime").getValue(String.class);
                            String desc = childDataSnapshot.child("HBdescription").getValue(String.class);
                            int value = childDataSnapshot.child("HBvalue").getValue(Integer.class);

                            if (childDataSnapshot != null) {
                                hbList.add(new HeartbeatHistoryList(date, time, desc, value));
                                hbCustomAdapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Toast.makeText(getActivity(),"History fetching failed",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                hbCustomAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read hb value.", error.toException());
            }
        });



        //Time Chart starts here

        hbChart.animateXY(3000, 3000);

        hbChart.getDescription().setEnabled(false);

        // enable touch gestures
        hbChart.setTouchEnabled(true);

        hbChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        hbChart.setDragEnabled(true);
        hbChart.setScaleEnabled(true);
        hbChart.setDrawGridBackground(false);
        hbChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        hbChart.setBackgroundColor(Color.WHITE);
        hbChart.setViewPortOffsets(40f, 10f, 0f, 35f);

        // add data
        setData(50, 50);
        hbChart.invalidate();

        //chart filling
        List<ILineDataSet> sets = hbChart.getData()
                .getDataSets();

        for (ILineDataSet iSet : sets) {

            LineDataSet set = (LineDataSet) iSet;
            set.setDrawFilled(true);
        }
        hbChart.invalidate();


        // get the legend (only possible after setting data)
        Legend l = hbChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = hbChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(6f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.US);

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis yAxis = hbChart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(ColorTemplate.getHoloBlue());
        yAxis.setDrawGridLines(true);
        yAxis.setGranularityEnabled(true);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setYOffset(3f);
        yAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = hbChart.getAxisRight();
        rightAxis.setEnabled(false);








        return heartbeatView;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




//NOT required until machine is ready
    private void addData(){


        XAxis xAxis = hbChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        hbHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    date = snapshot.getKey();

                    try {
                        String dateString = date;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = sdf.parse(dateString);

                         startDate = date.getTime();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    DatabaseReference keyReference = hbHistory.getRef().child(date).getRef();

                    keyReference.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot childDataSnapshot) {

                            Log.d(TAG, "onDataSnapshot: reached");
                            Log.d(TAG, "Key" + date);

                            Float avg = childDataSnapshot.child("HBavg").getValue(Float.class);
                            Log.d(TAG, "Value" + avg);

                            value.add(new Entry(startDate,avg));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Toast.makeText(getActivity(),"History Chart Update1 failed",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                LineDataSet set = new LineDataSet(value, "DataSet history");
                set.setAxisDependency(YAxis.AxisDependency.LEFT);


                // create a data object with the datasets
                LineData data2 = new LineData(set);
                data2.setValueTextColor(Color.WHITE);
                data2.setValueTextSize(9f);

                // set data
                hbChart.setData(data2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"History Chart Update2 failed",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setData(int count, float range) {
        // now in hours
        long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());

        Log.d("now",Long.toString(now));
        ArrayList<Entry> values = new ArrayList<Entry>();

        float from = now;

        // count = hours
        float to = now + count;

        // increment by 1 hour
        for (float x = from; x < to; x++) {
            float y = getRandom(range, 20);
            Log.d("value of x and y  :",x +"  " + y  );
            values.add(new Entry(x, y)); // add one entry per hour
        }
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        hbChart.setData(data);
    }

    protected float getRandom(float range, float startsfrom) {
        Log.d("Random func",Double.toString(Math.random()));
        return (float) (Math.random() * range) + startsfrom;
    }


    private void viewPdf(String file) {

        File pdfFile = new File(Objects.requireNonNull(getActivity()).getFilesDir().getAbsolutePath()  +  "smartWatch" + "/" + file);
        Uri path = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }








    }



