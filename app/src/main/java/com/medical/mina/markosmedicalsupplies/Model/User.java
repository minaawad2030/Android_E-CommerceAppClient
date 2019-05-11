package com.medical.mina.markosmedicalsupplies.Model;

/**
 * Created by Mina on 2/8/2018.
 */

public class User {

    private String name;
    private String password;
    private String phone;
    public User() {
    }

    public User(String Pname, String Ppassword) {


        name = Pname;
        password = Ppassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String setname(String Pname) {
        name = Pname;
        return name;
    }

    public String getName() {
        return name;
    }

    public String getpassword() {
        return password;
    }
}
