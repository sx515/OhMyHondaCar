package com.dicklight.ohmyhondacar.Service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.dicklight.ohmyhondacar.MyApp;
import com.mitsubishielectric.ada.appservice.strgsw.IStrgSwApService;
import com.mitsubishielectric.ada.appservice.strgsw.IStrgSwEventPriorityApServiceListener;


public class StrgSWListenerService extends Service implements ServiceConnection {
    private static final String TAG = "StrgSWListenerService";
    IStrgSwApService mstrgSwApService;
    StrgKeyEventListener mStrgKeyEventListener;

    private boolean meterShowStatus;

    private MyApp myApp;
    public StrgSWListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        myApp = (MyApp) getApplication();
        meterShowStatus = myApp.getProfileManager().isAutoMeter();
        mStrgKeyEventListener = new StrgKeyEventListener(this);
        Intent intent = new Intent();
        intent.setAction(IStrgSwApService.class.getName());
        if (!bindService(intent, this, BIND_AUTO_CREATE)){
            Toast.makeText(this, "按键服务注册失败", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "bind strgswKey service fail");
        }else{
            Log.i(TAG, "bind strgswKey service ok");
        }

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, "onServiceConnected");
        if ("com.mitsubishielectric.ada.appservice.strgsw.StrgSwApService".equals(name.getClassName())){
            mstrgSwApService = IStrgSwApService.Stub.asInterface(service);
            if (mstrgSwApService != null) {
                Log.d(TAG, "bind strgswKey service ok");
            }else{
                Log.d(TAG, "bind strgswKey service fail");
            }
            try {
                mstrgSwApService.registerStrgEventPriorityCallback(mStrgKeyEventListener, 15);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    class StrgKeyEventListener extends IStrgSwEventPriorityApServiceListener.Stub{
        private Context mContext;
        public StrgKeyEventListener(Context context){
            mContext = context;
        }
        @Override
        public boolean onNotifyStrgKeyEventPriority(int i, int i1, int i2) throws RemoteException {
            Log.i(TAG, "start type:" + i + " status:" + i1 + " functionId:" + i2);
            if (i2 == 16) {
                if (i == 12) {
                    if (i1 == 2) {
                        Log.i(TAG, "key long down");
                        if (myApp.getMeterStatus()) {
                            //仪表已经运行，则杀死
                            Intent intent = new Intent(mContext, MeterService.class);
                            stopService(intent);
                            myApp.setMeterStatus(false);
                        } else {
                            Intent meterService = new Intent(mContext, MeterService.class);
                            startService(meterService);
                            myApp.setMeterStatus(true);
                        }
                    }else if(i1 == 1){
                        Log.i(TAG, "key down");
                        Intent intent = new Intent(MeterService.ACTION_SCREENCAST_MODE_CHANGED);
                        intent.putExtra("isFullScreen", !myApp.getProfileManager().getScreenCastMode());
                        sendBroadcast(intent);
                        myApp.getProfileManager().setScreenCastMode(!myApp.getProfileManager().getScreenCastMode());
                    }

                }
                return true;
            }else if (i2 == 15 && i == 11){
                return false;
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mstrgSwApService != null){
            try {
                mstrgSwApService.unregisterStrgEventPriorityCallback(mStrgKeyEventListener, 15);
                unbindService(this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}