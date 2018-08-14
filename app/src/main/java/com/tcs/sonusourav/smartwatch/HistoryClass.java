package com.tcs.sonusourav.smartwatch;

/**
 * Created by SONU SOURAV on 5/13/2018.
 */

public class HistoryClass {
    private String text;
    private String date;
    private String time;
    private String deviceName;
    private int srNumber;

    public HistoryClass(String hisName, String hisDate, String hisTime, String hisDeviceName, int hisSRnumber) {
        this.text = hisName;
        this.date = hisDate;
        this.time = hisTime;
        this.deviceName = hisDeviceName;
        this.srNumber = hisSRnumber;

    }

    public String getText() {
        return text;
    }

    public void setText(String hisText) {
        this.text = hisText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String hisDate) {
        this.date = hisDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String hisTime) {
        this.time = hisTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String hisDevice) {
        this.deviceName = hisDevice;
    }

    public int getSrNumber() {
        return srNumber;
    }

    public void setSrNumber(int hisSRNumber) {
        this.srNumber = hisSRNumber;
    }


}
