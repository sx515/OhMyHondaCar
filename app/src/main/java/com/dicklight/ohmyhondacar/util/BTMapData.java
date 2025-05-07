package com.dicklight.ohmyhondacar.util;

public class BTMapData {
    private String btDevice_name;
    private String wifiSSID;
    private String wifiPassword;
    private int wifiPassMethod;

    private BTMapData(Builder builder){
        this.btDevice_name = builder.bt_name;
        this.wifiSSID = builder.wifi_ssid;
        this.wifiPassword = builder.wifi_password;
        if (builder.wifi_password.equals("open"))
            this.wifiPassMethod = WiFiConnector.ENCRYPTION_TYPE_OPEN;
        else{
            this.wifiPassMethod = builder.wifi_encryptionType;
        }
    }

    public MapDataBean getBean(){
        MapDataBean bean = new MapDataBean();
        bean.setBtDevice_name(this.btDevice_name);
        bean.setWifiSSID(this.wifiSSID);
        bean.setWifiPassword(this.wifiPassword);
        bean.setWifiPassMethod(this.wifiPassMethod);
        return bean;
    }

    public String getBtDevice_name()
    {
        return btDevice_name;
    }



    public String getWifiSSID()
    {
        return wifiSSID;
    }



    public String getWifiPassword()
    {
        return wifiPassword;
    }



    public int getWifiPassMethod()
    {
        return wifiPassMethod;
    }

    public static class Builder{
        private String bt_name;
        private String wifi_ssid;
        private String wifi_password = "open";
        private int wifi_encryptionType = WiFiConnector.ENCRYPTION_TYPE_WPA;
        public Builder(String bt_name, String wifi_ssid){
            this.bt_name = bt_name;
            this.wifi_ssid = wifi_ssid;
        }



        public Builder setWiFiPassword(String password){
            wifi_password = password;
            return this;
        }

        public Builder setWiFiEncryType(int type){
            wifi_encryptionType = type;
            return this;
        }

        public BTMapData build(){
            return new BTMapData(this);
        }
    }
}
