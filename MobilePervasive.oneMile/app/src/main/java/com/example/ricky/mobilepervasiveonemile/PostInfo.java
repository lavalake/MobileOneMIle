package com.example.ricky.mobilepervasiveonemile;

/**
 * Created by Ricky on 2/21/15.
 */
public class PostInfo {
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String Message = "Nothing happened. So sad :(";
    private String Address = "Here you are!";

    PostInfo(double latitude, double longitude, String Message, String Address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.Message = Message;
        this.Address = Address;
    }

    public void setParameter(String param, String info) {
        if (param.equalsIgnoreCase("latitude"))
            this.latitude = Integer.valueOf(info);
        if (param.equalsIgnoreCase("longitude"))
            this.longitude = Integer.valueOf(info);
        if (param.equalsIgnoreCase("Message"))
            this.Message = info;
        if (param.equalsIgnoreCase("Address"))
            this.Address = info;
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
    public String getAddress() {
        return this.Address;
    }

}
