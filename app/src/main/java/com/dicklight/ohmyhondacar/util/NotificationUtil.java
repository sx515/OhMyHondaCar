package com.dicklight.ohmyhondacar.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;

import com.dicklight.ohmyhondacar.R;

public class NotificationUtil {
    private int notifId;
    private String title;
    private Service mService;
    private int ICON = R.drawable.ic_launcher;

    public NotificationUtil(int notifId, String title, Service service){
        this.notifId = notifId;
        this.title = title;
    }



    public void showNotif(String NotifContent){
        Notification.Builder builder = new Notification.Builder(mService)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(title)
        .setContentText(NotifContent)
        .setWhen(System.currentTimeMillis())
        .setOngoing(true);

        Notification mNotification = builder.build();
        mService.startForeground(notifId, mNotification);
    }

    public void cancelNotification(){
        mService.stopForeground(true);
    }
}
