package com.tcs.sonusourav.smartwatch;

/**
 * Created by Administrator on 02-02-2018.
 */

public class UserClass {
    public String name;
    public String email;
    public String DOB;
    public String gender;
    public String phone;
    public String pass;


    public UserClass(String userName, String userEmail, String userDob, String userGender, String userPhone, String userPassword) {
        name = userName;
        phone = userPhone;
        email = userEmail;
        pass = userPassword;
        gender = userGender;
        DOB = userDob;
    }


}
