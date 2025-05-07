package com.dicklight.ohmyhondacar.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.dicklight.ohmyhondacar.MyApp;
import com.dicklight.ohmyhondacar.R;
import com.dicklight.ohmyhondacar.util.NotificationUtil;
import com.dicklight.ohmyhondacar.util.ScreenShotUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MeterService extends Service {
    private static final String TAG = "MeterService";
    private static final int NOTIF_ID = 1007;

    private View rootView;
    private SurfaceView mSurfaceView;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private DisplayManager mDisplayManager;
    private Display mDisplay;
    private Context exDisplayContext;

    private MyApp mApp;
    private boolean isRunsInDebugMode;
    private NotificationUtil mNotificationUtil;

    private ScheduledExecutorService mScheduledExecutorService;

    private boolean surfaceReady = false;
    private SurfaceHolder mSurfaceHolder;

    private Bitmap screenCast;
    private Paint backgroundPaint;
    private Rect backgroundRect;

    private boolean isFullScreen = true;
    private int EX_DISPLAY_WIDTH = 593;
    private int EX_DISPLAY_HEIGHT = 198;

    public static final String ACTION_SCREENCAST_MODE_CHANGED = "ACTION_SCREENCAST_MODE_CHANGED";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SCREENCAST_MODE_CHANGED.equals(intent.getAction())){
                isFullScreen = intent.getBooleanExtra("isFullScreen", true);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent p1)
    {
        // TODO: Implement this method
        return null;
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Meter Service start.");
        mApp = (MyApp)getApplication();
        isRunsInDebugMode = mApp.isAppRunsInDebugMode();
        isFullScreen = mApp.getProfileManager().getScreenCastMode();
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SCREENCAST_MODE_CHANGED);
        registerReceiver(receiver, intentFilter);
        initService();
    }


    private void initService() {
        //getSystemService(NOTIFICATION_SERVICE);
        //mNotificationUtil = new NotificationUtil(NOTIF_ID, "仪表投屏服务", this);
        //mNotificationUtil.showNotif("投屏服务运行中");
        mDisplayManager = (DisplayManager)getSystemService(DISPLAY_SERVICE);
        if (mDisplayManager == null){
            Toast.makeText(getApplicationContext(), "Can't bind displayManager service", Toast.LENGTH_SHORT).show();

        }else{
            Display[] displays = mDisplayManager.getDisplays();
            if (displays.length < 2){
                Toast.makeText(this, "副屏不存在, 使用最后一个屏幕", Toast.LENGTH_SHORT).show();
            }
            mDisplay = displays[displays.length - 1];
            exDisplayContext = createDisplayContext(mDisplay);
            mWindowManager = (WindowManager)exDisplayContext.getSystemService(WINDOW_SERVICE);
            rootView = LayoutInflater.from(this).inflate(R.layout.ex_display_layout, null);
            mSurfaceView = (SurfaceView)rootView.findViewById(R.id.exdisplaylayoutSurfaceView1);
            mSurfaceView.getHolder().addCallback(new SurfaceCallback());

            mLayoutParams = new WindowManager.LayoutParams(
                    EX_DISPLAY_WIDTH,
                    EX_DISPLAY_HEIGHT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
            );
            mLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;
            mLayoutParams.x = 10;
            mLayoutParams.y = 25;

            mWindowManager.addView(rootView, mLayoutParams);
            startScreenshotLoop();

        }

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundRect = new Rect(0, 0, EX_DISPLAY_WIDTH, EX_DISPLAY_HEIGHT);


    }



    private void startScreenshotLoop(){
        mScheduledExecutorService.scheduleWithFixedDelay(new Runnable(){

            @Override
            public void run() {
                //Log.i(TAG, "loop...");
                //周期性任务
                if (surfaceReady && mSurfaceHolder != null){
                    screenCast = getScreenshot();
                    if (screenCast != null){
                        Canvas mCanvas = null;

                        try{
                            mCanvas = mSurfaceHolder.lockCanvas();
                            mCanvas.drawRect(backgroundRect, backgroundPaint);
                            mCanvas.drawBitmap(screenCast, 0, 0, null);
                        }finally{
                            if (mCanvas != null){
                                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                            }
                        }

                    }
                }

            }
        }, 0, 1, TimeUnit.NANOSECONDS);
    }


    private Bitmap getScreenshot() {
        Bitmap screenContent;
        Bitmap systemScreen = null;
        try {
            systemScreen = ScreenShotUtil.getScreenBitmap();
            if (systemScreen == null){
                Toast.makeText(this, "截图失败", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        screenContent = Bitmap.createBitmap(593, 198, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenContent);
        if (!isFullScreen){
            //非全屏，只显示carlife屏幕右上角导航预告
            Bitmap nav_pre_info = Bitmap.createBitmap(systemScreen, 478, 10, 316, 152);
            Bitmap nav_way_info = Bitmap.createBitmap(systemScreen, 101, 24, 350, 93);
            Bitmap builtImage = Bitmap.createBitmap(350, nav_pre_info.getHeight() + nav_way_info.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas builtCanvas = new Canvas(builtImage);
            builtCanvas.drawBitmap(nav_pre_info, (float) (builtImage.getWidth() - nav_pre_info.getWidth()) /2, 0, null);
            builtCanvas.drawBitmap(nav_way_info, 0, nav_pre_info.getHeight(), null);
            drawCenterInside(canvas, builtImage);
            return screenContent;
        }else{
            //全屏模式，直接投屏
            drawCenterInside(canvas, systemScreen);
            return screenContent;
        }


    }

    private void drawCenterInside(Canvas canvas, Bitmap src){
        float scale = Math.min(
                (float)canvas.getWidth() / src.getWidth(),
                (float)canvas.getHeight() / src.getHeight()
        );
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        float dx = (canvas.getWidth() - src.getWidth() * scale) / 2;
        float dy = (canvas.getHeight() - src.getHeight() * scale) / 2;
        matrix.postTranslate(dx, dy);
        canvas.drawBitmap(src, matrix, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
    }

    class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surfaceCreated");
            mSurfaceHolder = holder;
            surfaceReady = true;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        mScheduledExecutorService.shutdown();
        if (mWindowManager != null){
            mWindowManager.removeView(rootView);
        }
        if (exDisplayContext != null){
            exDisplayContext.stopService(new Intent(exDisplayContext, MeterService.class));
        }
    }
}
