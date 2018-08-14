package com.tcs.sonusourav.smartwatch;

/**
 * Created by SONU SOURAV on 4/11/2018.
 */

public class EmergencyContactsList {
    private String ECname;
    private String ECphone;

    public EmergencyContactsList(String ecname, String ecphone) {
        this.ECname = ecname;
        this.ECphone = ecphone;
    }

    public String getECname() {
        return ECname;
    }

    public void setECname(String name) {
        this.ECname = name;
    }

    public String getECphone() {
        return ECphone;
    }

    public void setECphone(String phone) {
        this.ECphone = phone;
    }
}
