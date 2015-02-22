package com.example.ricky.mobilepervasiveonemile;

/**
 * Created by Ricky on 2/21/15.
 */
public class PostInfo {
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String Message = "Nothing happened. So sad :(";

    PostInfo(double latitude, double longitude, String Message) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.Message = Message;
    }

    public void setParameter(String param, String info) {
        if (param.equalsIgnoreCase("latitude"))
            this.latitude = Integer.valueOf(info);
        if (param.equalsIgnoreCase("longitude"))
            this.longitude = Integer.valueOf(info);
        if (param.equalsIgnoreCase("Message"))
            this.Message = info;
        else {
            System.out.println("Parameter Error. Please check!");
            System.exit(1);
        }
    }

    public double getLatitude() {
        return this.latitude;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public String getMessage() {
        return this.Message;
    }

}
