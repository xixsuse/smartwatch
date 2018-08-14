package com.tcs.sonusourav.smartwatch;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyXAxisValueFormatter extends IndexAxisValueFormatter {

    public String getXValue(String dateInMillisecons, int index, ViewPortHandler viewPortHandler) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");

            return sdf.format(new Date(Long.parseLong(dateInMillisecons)));
        } catch (Exception e) {
            return dateInMillisecons;
        }

    }
}