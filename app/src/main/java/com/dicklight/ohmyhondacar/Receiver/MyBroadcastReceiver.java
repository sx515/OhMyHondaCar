package com.dicklight.ohmyhondacar.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dicklight.ohmyhondacar.MyApp;
import com.dicklight.ohmyhondacar.Service.BTConnectListenerService;
import com.dicklight.ohmyhondacar.Service.MeterService;
import com.dicklight.ohmyhondacar.Service.StrgSWListenerService;

public class MyBroadcastReceiver extends BroadcastReceiver {
    MyApp myApp;
    @Override
    public void onReceive(Context context, Intent intent) {
        myApp = (MyApp) context.getApplicationContext();
        if (myApp.getProfileManager().isAutoMeter()) {
            Intent meterService = new Intent(context, MeterService.class);
            context.startService(meterService);
            myApp.setMeterStatus(true);
            Intent strgSWListenerService = new Intent(context, StrgSWListenerService.class);
            context.startService(strgSWListenerService);
        }
        if (myApp.getProfileManager().isAutoCarlife()) {
            Intent btConnectListenerService = new Intent(context, BTConnectListenerService.class);
            context.startService(btConnectListenerService);
        }
    }
}
