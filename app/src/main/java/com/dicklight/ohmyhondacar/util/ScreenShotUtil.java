package com.dicklight.ohmyhondacar.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class ScreenShotUtil {
    private static final int screenWidth = 800;
    private static final int screenHeight = 480;
    private static final boolean isDebug = false;
    static {
        System.loadLibrary("ohmyhondacar");
    }
    private static native byte[] captureScreen(int width, int height);

    public static Bitmap getScreenBitmap() throws IOException {
        if (isDebug) {
            try(FileInputStream fis = new FileInputStream("/sdcard/screen.png")){
                return BitmapFactory.decodeStream(fis);
            }
        }
        byte[] piex = captureScreen(screenWidth, screenHeight);
        int[] colors = new int[screenHeight * screenWidth];
        for (int m = 0; m < colors.length; m++) {

            int r = (piex[m * 4] & 0xFF);
            int g = (piex[m * 4 + 1] & 0xFF);
            int b = (piex[m * 4 + 2] & 0xFF);
            int a = (piex[m * 4 + 3] & 0xFF);
            colors[m] = (a << 24) + (r << 16) + (g << 8) + b;
        }

        return Bitmap.createBitmap(colors, screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
    }
}
