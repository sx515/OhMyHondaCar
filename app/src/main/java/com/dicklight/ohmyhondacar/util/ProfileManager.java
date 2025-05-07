package com.dicklight.ohmyhondacar.util;

import android.content.SharedPreferences;

import com.dicklight.ohmyhondacar.MyApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import java.lang.reflect.Type;

public class ProfileManager {
    private static final String PF_FILE_NAME = "hondacarcfg";
    public static final String PF_AUTO_CARLIFE = "auto_carlife";
    public static final String PF_AUTO_METER = "auto_meter";
    public static final String PF_BT_WIFI_MAP_LIST = "bt2wifiMap";
    public static final String PF_APP_RUN_IN_DEBUG_MODE = "appRunInDebugMode";
    public static final String PF_SCREEN_CAST_MODE = "screenCastMode";

    private MyApp mApp;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public ProfileManager(MyApp app){
        this.mApp = app;
        mSharedPreferences = app.getSharedPreferences(PF_FILE_NAME, app.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private void setBool(String node, boolean v){
        mEditor.putBoolean(node, v);
        mEditor.commit();
    }

    private boolean getBool(String node){
        return mSharedPreferences.getBoolean(node, false);
    }

    public void enableAutoStartCarlife(boolean b){
        setBool(PF_AUTO_CARLIFE, b);
    }

    public boolean isAutoCarlife(){
        return getBool(PF_AUTO_CARLIFE);
    }

    public void enableAutoMeter(boolean b){
        setBool(PF_AUTO_METER, b);
    }

    public boolean isAutoMeter(){
        return getBool(PF_AUTO_METER);
    }

    private void setString(String node, String Content){
        mEditor.putString(node, Content);
        mEditor.commit();
    }

    private String getString(String node){
        return mSharedPreferences.getString(node, "null");
    }

    public void saveBT2WiFiMap(List<MapDataBean> list){
        Gson mGson = new Gson();
        String jsonStr = mGson.toJson(list);
        setString(PF_BT_WIFI_MAP_LIST, jsonStr);
    }

    public List<MapDataBean> getBT2WiFiMapList(){
        String jsonStr = getString(PF_BT_WIFI_MAP_LIST);
        if (jsonStr == null){
            return null;
        }
        Gson mGson = new Gson();
        Type listType = new TypeToken<List<MapDataBean>>(){}.getType();
        return mGson.fromJson(jsonStr, listType);
    }

    public boolean getRunMode(){
        return getBool(PF_APP_RUN_IN_DEBUG_MODE);
    }

    public void setDebugMode(boolean v){
        setBool(PF_APP_RUN_IN_DEBUG_MODE, v);
    }

    public boolean getScreenCastMode(){
        return getBool(PF_SCREEN_CAST_MODE);
    }

    public void setScreenCastMode(boolean v){
        setBool(PF_SCREEN_CAST_MODE, v);
    }
}
