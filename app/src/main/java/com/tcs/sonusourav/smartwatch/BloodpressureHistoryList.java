package com.tcs.sonusourav.smartwatch;

public class BloodpressureHistoryList {

    private String BPhistoryDate;
    private String BPhistoryTime;
    private String BPdescription;
    private int BPvalue;


    BloodpressureHistoryList(String bpDate, String bpTime, String bpDesc, int bpValue) {
        this.BPhistoryDate = bpDate;
        this.BPhistoryTime = bpTime;
        this.BPdescription = bpDesc;
        this.BPvalue = bpValue;

        }


    public String getHBhistoryDate() {
        return BPhistoryDate;
    }

    public void setHBhistoryDate(String date) {
        this.BPhistoryDate = date;
    }

    public String getHBhistoryTime() {
        return BPhistoryTime;
    }

    public void setHBhistoryTime(String time) {
        this.BPhistoryTime = time;
    }

    public String getHBdescription() {
        return BPdescription;
    }

    public void setHBdescription(String desc) {
        this.BPdescription = desc;
    }

    public int getHBvalue() {
        return BPvalue;
    }

    public void setHBvalue(int val) {
        this.BPvalue = val;
    }
}


