package com.tcs.sonusourav.smartwatch;

public class HeartbeatHistoryList {
    private String HBhistoryDate;
    private String HBhistoryTime;
    private String HBdescription;
    private int HBvalue;


    public HeartbeatHistoryList(String hbDate, String hbTime, String hbDesc, int hbValue) {
        this.HBhistoryDate = hbDate;
        this.HBhistoryTime = hbTime;
        this.HBdescription = hbDesc;
        this.HBvalue = hbValue;
    }

    public String getHBhistoryDate() {
        return HBhistoryDate;
    }

    public void setHBhistoryDate(String date) {
        this.HBhistoryDate = date;
    }

    public String getHBhistoryTime() {
        return HBhistoryTime;
    }

    public void setHBhistoryTime(String time) {
        this.HBhistoryTime = time;
    }

    public String getHBdescription() {
        return HBdescription;
    }

    public void setHBdescription(String desc) {
        this.HBdescription = desc;
    }

    public int getHBvalue() {
        return HBvalue;
    }

    public void setHBvalue(int val) {
        this.HBvalue = val;
    }
}
