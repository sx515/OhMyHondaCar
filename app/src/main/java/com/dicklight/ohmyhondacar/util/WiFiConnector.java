package com.dicklight.ohmyhondacar.util;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

public class WiFiConnector {
    private static final String TAG = "WiFiConnector";


    public static final int ENCRYPTION_TYPE_WPA = 1;
    public static final int ENCRYPTION_TYPE_WPA2 = 2;
    public static final int ENCRYPTION_TYPE_WEP =3;
    public static final int ENCRYPTION_TYPE_OPEN = 0;


    private final WifiManager wifiManager;

    public WiFiConnector(Context context) {
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 连接到指定 WiFi
     * @param ssid      WiFi 名称
     * @param password  WiFi 密码（无密码时传空）
     * @param type      加密类型
     * @return 是否连接成功
     */
    public boolean connectTo(String ssid, String password, int type) {
        if (wifiManager == null) {
            Log.e(TAG, "WifiManager is null!");
            return false;
        }

        // 检查 WiFi 是否已启用
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiConfiguration config = createWifiConfig(ssid, password, type);
        if (config == null) {
            return false;
        }

        // 尝试连接
        int networkId = wifiManager.addNetwork(config);
        if (networkId == -1) {
            Log.e(TAG, "Failed to add network configuration");
            return false;
        }

        // 保存配置（旧版本需要显式保存）
        boolean saveSuccess = wifiManager.saveConfiguration();
        if (!saveSuccess) {
            Log.w(TAG, "Failed to save configuration");
        }

        // 启用网络
        boolean enableSuccess = wifiManager.enableNetwork(networkId, true);
        if (!enableSuccess) {
            Log.e(TAG, "Failed to enable network");
            return false;
        }

        return true;
    }

    /**
     * 创建 WiFi 配置
     */
    private WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = formatSsid(ssid);

        switch (type) {
            case ENCRYPTION_TYPE_WPA:
            case ENCRYPTION_TYPE_WPA2:
                setupWpaConfig(config, password);
                break;
            case ENCRYPTION_TYPE_WEP:
                setupWepConfig(config, password);
                break;
            case ENCRYPTION_TYPE_OPEN:
                setupOpenConfig(config);
                break;
            default:
                Log.e(TAG, "Unsupported encryption type");
                return null;
        }

        return config;
    }

    /**
     * 处理 WPA/WPA2 加密
     */
    private void setupWpaConfig(WifiConfiguration config, String password) {
        config.preSharedKey = "\"" + password + "\"";
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // 兼容旧设备
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.status = WifiConfiguration.Status.ENABLED;
    }

    /**
     * 处理 WEP 加密
     */
    private void setupWepConfig(WifiConfiguration config, String password) {
        config.wepKeys[0] = "\"" + password + "\"";
        config.wepTxKeyIndex = 0;
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
    }

    /**
     * 处理开放网络（无密码）
     */
    private void setupOpenConfig(WifiConfiguration config) {
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.allowedAuthAlgorithms.clear();
    }

    /**
     * 格式化 SSID（确保带引号）
     */
    private String formatSsid(String ssid) {
        if (!ssid.startsWith("\"") && !ssid.endsWith("\"")) {
            return "\"" + ssid + "\"";
        }
        return ssid;
    }

    /**
     * 兼容旧版本：强制保存配置（通过反射）
     */
    @SuppressWarnings("unused")
    private boolean saveConfigurationReflection() {
        try {
            Method save = wifiManager.getClass().getMethod("saveConfiguration");
            return (Boolean) save.invoke(wifiManager);
        } catch (Exception e) {
            Log.e(TAG, "Reflection save failed: " + e.getMessage());
            return false;
        }
    }

    public static String getEncryptionTypeStr(int type){
        switch(type){
            case ENCRYPTION_TYPE_WEP:
                return "WEP";
            case ENCRYPTION_TYPE_OPEN:
                return "OPEN";
            case ENCRYPTION_TYPE_WPA:
                return "WPA";
            case ENCRYPTION_TYPE_WPA2:
                return "WPA2";
        }
        return null;//never
    }
}
