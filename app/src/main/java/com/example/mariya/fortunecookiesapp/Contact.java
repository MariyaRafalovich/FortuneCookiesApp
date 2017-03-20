package com.example.mariya.fortunecookiesapp;

import java.io.Serializable;

public class Contact implements Serializable {

    private String idfcookies;
    private String des;

    public Contact(String idfcookies, String des) {
        this.idfcookies = idfcookies;
        this.des = des;
    }

    public String getIdfcookies() {
        return idfcookies;
    }

    public String getDes() {
        return des;
    }

    public void setIdfcookies(String idfcookies) {
        this.idfcookies = idfcookies;
    }

    public void setDes(String des) {
        this.des = des;
    }
}


