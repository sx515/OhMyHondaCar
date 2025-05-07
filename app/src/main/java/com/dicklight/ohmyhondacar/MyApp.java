package com.dicklight.ohmyhondacar;

import android.app.Application;

import com.dicklight.ohmyhondacar.util.MapDataBean;
import com.dicklight.ohmyhondacar.util.ProfileManager;
import com.dicklight.ohmyhondacar.util.WiFiConnector;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {
    private ProfileManager profileManager;
    private List<MapDataBean> mapDataBeanList;
    private WiFiConnector wiFiConnector;
    private boolean appRunsInDebugMode = false;
    private boolean meterStatus = false;
    @Override
    public void onCreate() {
        super.onCreate();
        profileManager = new ProfileManager(this);
        updateBT2WiFiMapList();
        wiFiConnector = new WiFiConnector(this);
        this.appRunsInDebugMode = profileManager.getRunMode();

    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public void updateBT2WiFiMapList() {
        mapDataBeanList = profileManager.getBT2WiFiMapList();
        if (mapDataBeanList == null){
            mapDataBeanList = new ArrayList<>();
        }
    }

    public List<MapDataBean> getMapDataBeanList() {
        return mapDataBeanList;
    }

    public WiFiConnector getWiFiConnector() {
        return wiFiConnector;
    }

    public boolean isAppRunsInDebugMode() {
        return appRunsInDebugMode;
    }

    public void killMySelf(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public boolean getMeterStatus(){
        return meterStatus;
    }

    public void setMeterStatus(boolean b){
        this.meterStatus = b;
    }
}
