package com.dicklight.ohmyhondacar.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.dicklight.ohmyhondacar.MyApp;
import com.dicklight.ohmyhondacar.R;
import com.dicklight.ohmyhondacar.util.BTMapData;
import com.dicklight.ohmyhondacar.util.MapDataBean;
import com.dicklight.ohmyhondacar.util.WiFiConnector;

import java.util.List;

public class MapInputActivity extends Activity {
    private RadioGroup radioGroup;
    private EditText bt_name;
    private EditText wifi_ssid;
    private EditText wifi_password;
    private Button save_btn;

    private BTMapData.Builder builder;
    private int encryptionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_input);
        radioGroup = (RadioGroup) findViewById(R.id.btmapinputactivityRadioGroup1_wifi_encryption_type);
        radioGroup.check(R.id.btmapinputactivityRadioButton1_wifi_encryption_type_open);
        bt_name = (EditText) findViewById(R.id.btmapinputactivityEditText1_bt_name);
        wifi_ssid = (EditText) findViewById(R.id.btmapinputactivityEditText1_wifi_ssid);
        wifi_password = (EditText) findViewById(R.id.btmapinputactivityEditText1_wifi_password);
        save_btn = (Button) findViewById(R.id.btmapinputactivityButton1_save_config);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt_name.getText().toString().equals("")){
                    Toast.makeText(MapInputActivity.this, "蓝牙名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (wifi_ssid.getText().toString().equals("")){
                    Toast.makeText(MapInputActivity.this, "SSID名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                builder = new BTMapData.Builder(bt_name.getText().toString(), wifi_ssid.getText().toString());
                if (!wifi_password.getText().toString().equals("")){
                    //密码为空
                    if (wifi_password.getText().toString().length()<8){
                        Toast.makeText(MapInputActivity.this, "密码不合理", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        builder.setWiFiPassword(wifi_password.getText().toString());
                        if (encryptionType == WiFiConnector.ENCRYPTION_TYPE_OPEN){
                            Toast.makeText(MapInputActivity.this, "加密模式选择不合理", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        builder.setWiFiEncryType(encryptionType);
                    }
                }

                MyApp app = (MyApp) getApplication();
                List<MapDataBean> mapDataBeanList = app.getMapDataBeanList();
                mapDataBeanList.add(builder.build().getBean());
                app.getProfileManager().saveBT2WiFiMap(mapDataBeanList);
                app.updateBT2WiFiMapList();
                Toast.makeText(MapInputActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btmapinputactivityRadioButton1_wifi_encryption_type_open){
                    encryptionType = WiFiConnector.ENCRYPTION_TYPE_OPEN;
                } else if (checkedId == R.id.btmapinputactivityRadioButton1_wifi_encryption_type_wep){
                    encryptionType = WiFiConnector.ENCRYPTION_TYPE_WEP;
                } else if (checkedId == R.id.btmapinputactivityRadioButton1_wifi_encryption_type_wpa) {
                    encryptionType = WiFiConnector.ENCRYPTION_TYPE_WPA;
                } else if (checkedId == R.id.btmapinputactivityRadioButton1_wifi_encryption_type_wpa2) {
                    encryptionType = WiFiConnector.ENCRYPTION_TYPE_WPA2;
                }
                else {
                    encryptionType = WiFiConnector.ENCRYPTION_TYPE_OPEN;
                }
            }
        });
    }
}