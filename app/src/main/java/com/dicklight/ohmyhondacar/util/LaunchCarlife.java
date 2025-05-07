package com.dicklight.ohmyhondacar.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LaunchCarlife {
    public static void startCarlife(Context c){
        Intent carlifeMainActivity = new Intent();
        carlifeMainActivity.setClassName("com.baidu.carlifevehicle", "com.baidu.carlifevehicle.CarlifeActivity");
        carlifeMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            c.startActivity(carlifeMainActivity);
            Toast.makeText(c, "启动CarLife...", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            new AlertDialog.Builder(c).setTitle("错误")
                    .setMessage("车机没有安装Carlife")
                    .setCancelable(false)
                    .setPositiveButton("确定", null)
                    .show();
        }
    }
}
