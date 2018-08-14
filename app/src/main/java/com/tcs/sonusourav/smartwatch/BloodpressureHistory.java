package com.tcs.sonusourav.smartwatch;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BloodpressureHistory extends android.support.v4.app.Fragment {

    ListView bplistView;
    ArrayList<BloodpressureHistoryList> bpList;
    private BloodpressureAdapterM bpCustomAdapter;
    private FirebaseDatabase bpInstance;
    private DatabaseReference bpRootRef;
    private DatabaseReference bpEmailRef;
    private DatabaseReference bpHistory;
    private FirebaseUser bpuser;

    private LineChart bpChart;

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    private String TAG=this.getClass().getCanonicalName();

    public static BloodpressureHistory newInstance() {
        return new BloodpressureHistory();
    }

    public static android.support.v4.app.Fragment newInstance(Bundle savedInstanceState) {

        android.support.v4.app.Fragment fragment = new BloodpressureHistory();
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

        View bloodpressureView = inflater.inflate(R.layout.history_bloodpressure, container, false);
        bplistView = bloodpressureView.findViewById(R.id.bloodpressure_listview);
        bpList = new ArrayList<>();
        bpChart = bloodpressureView.findViewById(R.id.bloodpressure_linechart);



        FirebaseAuth bpAuth = FirebaseAuth.getInstance();
        bpInstance = FirebaseDatabase.getInstance();
        bpRootRef = bpInstance.getReference("Users");
        bpuser = bpAuth.getCurrentUser();
        assert bpuser != null;
        String testEmail = encodeUserEmail(Objects.requireNonNull(bpuser.getEmail()));
        bpEmailRef = bpRootRef.child(testEmail).getRef();
        bpHistory = bpEmailRef.child("BloodpressureHistory").getRef();


            bpCustomAdapter = new BloodpressureAdapterM(getActivity(),bpList);
            bplistView.setAdapter(bpCustomAdapter);


            bpHistory.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        Log.d(TAG, "Key" + key);
                        DatabaseReference keyReference = bpHistory.getRef().child(key).getRef();
                        Log.d(TAG, "onDataChangeDate: reached");

                        keyReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot childDataSnapshot) {
                                String date = childDataSnapshot.child("BPhistoryDate").getValue(String.class);
                                String time = childDataSnapshot.child("BPhistoryTime").getValue(String.class);
                                String desc = childDataSnapshot.child("BPdescription").getValue(String.class);
                                int value = childDataSnapshot.child("BPvalue").getValue(Integer.class);

                                if (childDataSnapshot != null) {
                                    bpList.add(new BloodpressureHistoryList(date, time, desc, value));
                                    bpCustomAdapter.notifyDataSetChanged();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(),"History fetching failed",Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                    bpCustomAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read bp value.", error.toException());
                }
            });



//time chart

        //Time Chart starts here

        bpChart.animateXY(3000, 3000);

        bpChart.getDescription().setEnabled(false);

        // enable touch gestures
        bpChart.setTouchEnabled(true);

        bpChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        bpChart.setDragEnabled(true);
        bpChart.setScaleEnabled(true);
        bpChart.setDrawGridBackground(false);
        bpChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        bpChart.setBackgroundColor(Color.WHITE);
        bpChart.setViewPortOffsets(40f, 10f, 0f, 35f);

        // add data
        setData(50,50);
        bpChart.invalidate();

        //chart filling
        List<ILineDataSet> sets = bpChart.getData()
                .getDataSets();

        for (ILineDataSet iSet : sets) {

            LineDataSet set = (LineDataSet) iSet;
            set.setDrawFilled(true);
        }
        bpChart.invalidate();


        // get the legend (only possible after setting data)
        Legend l = bpChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = bpChart.getXAxis();
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

        YAxis yAxis = bpChart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextColor(ColorTemplate.getHoloBlue());
        yAxis.setDrawGridLines(true);
        yAxis.setGranularityEnabled(true);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setYOffset(3f);
        yAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = bpChart.getAxisRight();
        rightAxis.setEnabled(false);








        return bloodpressureView;
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
        bpChart.setData(data);
    }

    protected float getRandom(float range, float startsfrom) {
        Log.d("Random func",Double.toString(Math.random()));
        return (float) (Math.random() * range) + startsfrom;
    }



}

