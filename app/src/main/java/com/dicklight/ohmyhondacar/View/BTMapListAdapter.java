package com.dicklight.ohmyhondacar.View;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dicklight.ohmyhondacar.MyApp;
import com.dicklight.ohmyhondacar.R;
import com.dicklight.ohmyhondacar.util.MapDataBean;
import com.dicklight.ohmyhondacar.util.WiFiConnector;

import java.util.List;

public class BTMapListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MapDataBean> mDataList;

    public BTMapListAdapter(Context context, List<MapDataBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder;
       if (convertView == null) {
           convertView = View.inflate(mContext, R.layout.bt_wifi_setting_listview, null);
           holder = new ViewHolder();
           holder.bt_name = (TextView) convertView.findViewById(R.id.btwifisettinglistviewTextView1_bt_name);
           holder.wifi_ssid = (TextView) convertView.findViewById(R.id.btwifisettinglistviewTextView1_wifi_ssid);
           holder.wifi_password = (TextView) convertView.findViewById(R.id.btwifisettinglistviewTextView1_wifi_password);
           holder.wifi_encryptionType = (TextView) convertView.findViewById(R.id.btwifisettinglistviewTextView1_pass_method);
           holder.remove_item = (Button) convertView.findViewById(R.id.btwifisettinglistviewButton1_del_item);
           convertView.setTag(holder);
       } else {
           holder = (ViewHolder) convertView.getTag();
       }

       MapDataBean itemData = mDataList.get(position);
       holder.bt_name.setText("蓝牙名称：" + itemData.getBtDevice_name());
       holder.wifi_ssid.setText("SSID：" + itemData.getWifiSSID());
       holder.wifi_password.setText("密码：" + itemData.getWifiPassword());
       holder.wifi_encryptionType.setText("加密方式：" + WiFiConnector.getEncryptionTypeStr(itemData.getWifiPassMethod()));
       holder.remove_item.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mDataList.remove(position);
               MyApp myApp = (MyApp) mContext.getApplicationContext();
               myApp.getProfileManager().saveBT2WiFiMap(mDataList);
               notifyDataSetChanged();
           }
       });

       return convertView;
    }

    public void setData(List<MapDataBean> mapDataBeanList) {
        this.mDataList = mapDataBeanList;
        //notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView bt_name;
        TextView wifi_ssid;
        TextView wifi_password;
        TextView wifi_encryptionType;
        Button remove_item;

    }
}
