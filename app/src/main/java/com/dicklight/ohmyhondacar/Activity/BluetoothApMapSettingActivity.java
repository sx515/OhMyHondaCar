package com.dicklight.ohmyhondacar.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dicklight.ohmyhondacar.MyApp;
import com.dicklight.ohmyhondacar.R;
import com.dicklight.ohmyhondacar.View.BTMapListAdapter;
import com.dicklight.ohmyhondacar.util.MapDataBean;
import com.dicklight.ohmyhondacar.util.WiFiConnector;

import java.util.List;

public class BluetoothApMapSettingActivity extends Activity {
    private ListView listView;
    private BTMapListAdapter adapter;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_ap_map_setting);
        myApp = (MyApp) getApplication();

        listView = (ListView) findViewById(R.id.btwifimapsettingListView1_config_list);
        Button addNew = (Button) findViewById(R.id.btwifimapsettingButton1_add_new_config);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothApMapSettingActivity.this, MapInputActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MapDataBean mapDataBean = (MapDataBean) adapter.getItem(position);
                WiFiConnector wiFiConnector = new WiFiConnector(BluetoothApMapSettingActivity.this);
                if (wiFiConnector.connectTo(mapDataBean.getWifiSSID(), mapDataBean.getWifiPassword(), mapDataBean.getWifiPassMethod())){
                    Toast.makeText(BluetoothApMapSettingActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BluetoothApMapSettingActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<MapDataBean> mapDataBeanList = myApp.getMapDataBeanList();
        if (adapter != null){
            adapter.setData(mapDataBeanList);
            adapter.notifyDataSetChanged();
        }else{
            adapter = new BTMapListAdapter(this, mapDataBeanList);
            listView.setAdapter(adapter);
        }
    }
}