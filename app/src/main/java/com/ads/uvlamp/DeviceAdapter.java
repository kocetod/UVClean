package com.ads.uvlamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceAdapter extends ArrayAdapter<EspDevice> implements View.OnClickListener{

    private ArrayList<EspDevice> dataSet;
    Context mContext;
    TextView number;
    TextView ipAddress;
    TextView macAddress;
    ImageView icon;

    // View lookup cache
    private static class ViewHolder {

    }

    public DeviceAdapter(ArrayList<EspDevice> data, Context context) {
        super(context, R.layout.network_device, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        EspDevice dataModel=(EspDevice) object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.network_device, parent, false);

        EspDevice dataModel = getItem(position);

        //number = (TextView) theView.findViewById(R.id.item_num);
        ipAddress = (TextView) theView.findViewById(R.id.ip_address);
        macAddress = (TextView) theView.findViewById(R.id.mac_address);

        icon = (ImageView)theView.findViewById(R.id.icon);


        ipAddress.setText(dataModel.getDeviceIp());
        //number.setText(String.valueOf(dataModel.getDeviceNum()));
        macAddress.setText(dataModel.getDeviceMac());
        icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_devices_24));


        return theView;
    }
}