package com.dicklight.ohmyhondacar.util;

public class MapDataBean {
    public String getBtDevice_name() {
        return btDevice_name;
    }

    public void setBtDevice_name(String btDevice_name) {
        this.btDevice_name = btDevice_name;
    }

    private String btDevice_name;

    public String getWifiSSID() {
        return wifiSSID;
    }

    public void setWifiSSID(String wifiSSID) {
        this.wifiSSID = wifiSSID;
    }

    private String wifiSSID;

    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }

    private String wifiPassword;

    public int getWifiPassMethod() {
        return wifiPassMethod;
    }

    public void setWifiPassMethod(int wifiPassMethod) {
        this.wifiPassMethod = wifiPassMethod;
    }

    private int wifiPassMethod;

    public String dump(){
        StringBuilder sb = new StringBuilder();
        sb.append("BT Name: ");
        sb.append(btDevice_name);
        sb.append("\n");
        sb.append("WIFI SSID: ");
        sb.append(wifiSSID);
        sb.append("\n");
        sb.append("WIFI PASSWORD: ");
        sb.append(wifiPassword);
        sb.append("\n");

        return sb.toString();
    }

}
