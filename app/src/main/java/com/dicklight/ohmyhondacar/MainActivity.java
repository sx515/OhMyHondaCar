package com.dicklight.ohmyhondacar;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.Switch;

import com.dicklight.ohmyhondacar.Activity.BluetoothApMapSettingActivity;
import com.dicklight.ohmyhondacar.Service.BTConnectListenerService;
import com.dicklight.ohmyhondacar.Service.MeterService;
import com.dicklight.ohmyhondacar.Service.StrgSWListenerService;
import com.dicklight.ohmyhondacar.util.LaunchCarlife;

public class MainActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
private static final String TAG = "MainActivity";
    private  Button startCarfile;
    private Button startMeterService;
    private Button stopMeterService;
    private Button stopKeyListenerService;
    private Button startBT2WiFiMapSetting;
    private Switch autoStartBluetoothListenerService;
    private Switch autoStartMeterService;
    private Switch screenCastMode;

    private Button startStrgSwService;

    private MyApp myApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApp = (MyApp) getApplication();
        initView();
    }

    private void initView() {
        startCarfile = (Button) findViewById(R.id.mainButton1_start_carlife);
        if (startCarfile == null){
            Log.e(TAG,"startCarfile is null");
        }
        startCarfile.setOnClickListener(this);
        startMeterService = (Button) findViewById(R.id.mainButton1_start_meter_service);
        startMeterService.setOnClickListener(this);
        stopMeterService = (Button) findViewById(R.id.mainButton1_stop_meter_service);
        stopMeterService.setOnClickListener(this);
        stopKeyListenerService = (Button) findViewById(R.id.mainButton1_stop_strgsw_listener_service);
        stopKeyListenerService.setOnClickListener(this);
        startBT2WiFiMapSetting = (Button) findViewById(R.id.mainButton1_bt_ap_map_manager);
        startBT2WiFiMapSetting.setOnClickListener(this);
        startStrgSwService = (Button)findViewById(R.id.mainButton2_start_strgsw_listener_service);
        startStrgSwService.setOnClickListener(this);

        autoStartBluetoothListenerService = (Switch) findViewById(R.id.mainSwitch1_auto_run_bt_listener_service);
        autoStartMeterService = (Switch) findViewById(R.id.mainSwitch1_auto_run_meter_service);
        screenCastMode = (Switch) findViewById(R.id.mainButton1_switch_screencast_mode);

        if (myApp.getProfileManager().isAutoCarlife()){
            autoStartBluetoothListenerService.setChecked(true);
        }
        if (myApp.getProfileManager().isAutoMeter()){
            autoStartMeterService.setChecked(true);
        }
        if (myApp.getProfileManager().getScreenCastMode()){
            screenCastMode.setChecked(true);
        }
        autoStartBluetoothListenerService.setOnCheckedChangeListener(this);
        autoStartMeterService.setOnCheckedChangeListener(this);
        screenCastMode.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View v) {
       if (v.getId() == R.id.mainButton1_start_carlife){
           if (!Build.BOARD.equals("MAKO")) {
               LaunchCarlife.startCarlife(this);
           }
           Intent btlistenerService = new Intent(MainActivity.this, BTConnectListenerService.class);
           startService(btlistenerService);
       }
       if (v.getId() == R.id.mainButton1_start_meter_service){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MeterService.class);
            startService(intent);

       }

       if (v.getId() == R.id.mainButton2_start_strgsw_listener_service){
           Log.i(TAG, Build.BOARD);
           if (!Build.BOARD.equals("MAKO")) {
               Intent strgSWListenerServiceIntent = new Intent();
               strgSWListenerServiceIntent.setClass(MainActivity.this, StrgSWListenerService.class);
               startService(strgSWListenerServiceIntent);
           }
       }
       if (v.getId() == R.id.mainButton1_stop_meter_service){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MeterService.class);
            stopService(intent);
       }
       if (v.getId() == R.id.mainButton1_stop_strgsw_listener_service){
           Intent intent = new Intent();
           intent.setClass(MainActivity.this, StrgSWListenerService.class);
           stopService(intent);
       }
       if (v.getId() == R.id.mainButton1_bt_ap_map_manager){
           Intent i = new Intent();
           i.setClass(this, BluetoothApMapSettingActivity.class);
           startActivity(i);
       }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.mainSwitch1_auto_run_bt_listener_service){
            myApp.getProfileManager().enableAutoStartCarlife(isChecked);
        }
        if (buttonView.getId() == R.id.mainSwitch1_auto_run_meter_service){
            myApp.getProfileManager().enableAutoMeter(isChecked);
        }

        if (buttonView.getId() == R.id.mainButton1_switch_screencast_mode){
            myApp.getProfileManager().setScreenCastMode(isChecked);
            Intent intent = new Intent(MeterService.ACTION_SCREENCAST_MODE_CHANGED);
            intent.putExtra("isFullScreen", isChecked);
            sendBroadcast(intent);
        }
    }

}