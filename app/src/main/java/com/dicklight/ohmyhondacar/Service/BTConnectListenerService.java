package com.dicklight.ohmyhondacar.Service;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.dicklight.ohmyhondacar.MyApp;
import com.dicklight.ohmyhondacar.util.LaunchCarlife;
import com.dicklight.ohmyhondacar.util.MapDataBean;
import com.dicklight.ohmyhondacar.util.NotificationUtil;

public class BTConnectListenerService extends Service {
    private static final String TAG = "BTListenerService";
    private NotificationUtil notificationUtil;
    private static final int NOTIF_ID = 1006;
    private MyApp myApp;
    private BroadcastReceiver broadcastReceiver;
    private BluetoothDevice bluetoothDevice;
    private int bluetoothState;
    private boolean isConnect;
    private MapDataBean dataBean;
    public BTConnectListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        myApp = (MyApp) getApplication();
        //notificationUtil = new NotificationUtil(NOTIF_ID, "蓝牙监听服务", this);
        //updateNotif(false);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "in bluetooth broadcastReceiver");
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())){
                    bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "connect to BT : " + bluetoothDevice.getName());
                    for (MapDataBean dataBean1 : myApp.getMapDataBeanList()){
                        if (dataBean1.getBtDevice_name().equals(bluetoothDevice.getName())){
                            dataBean = dataBean1;
                            break;
                        }
                    }

                    if (dataBean != null){
                        Log.i(TAG, dataBean.dump());
                        LaunchCarlife.startCarlife(context);
                        myApp.getWiFiConnector().connectTo(dataBean.getWifiSSID(), dataBean.getWifiPassword(), dataBean.getWifiPassMethod());
                    }else{
                        Log.e(TAG, "bt device is not target");
                    }
                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                    bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (bluetoothState == BluetoothAdapter.STATE_OFF){
                    }
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
                    isConnect = false;
                }
            }
        };
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        ifilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        ifilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, ifilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}