package com.ads.uvlamp.ui.devices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ads.uvlamp.DeviceAdapter;
import com.ads.uvlamp.EspDevice;
import com.ads.uvlamp.R;

import com.ads.uvlamp.UserSettings;
import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import java.util.ArrayList;

public class DeviceFragment extends Fragment {

    private DeviceViewModel galleryViewModel;
    private ArrayList<Device> listOfDevices;
    private DeviceAdapter deviceAdapter;
    private ListView deviceListView;
    private Button scanBtn;
    private ArrayList<EspDevice> deviceList;
    private EspDevice foundDevice;
    private UserSettings userSettings;
    private ProgressBar progressBar;


    private String MAC_HEADER = "48:3f:da";

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(DeviceViewModel.class);

        View root = inflater.inflate(R.layout.fragment_devices, container, false);
        context = getActivity().getApplicationContext();

        userSettings = new UserSettings(context);
        deviceListView = (ListView)root.findViewById(R.id.deviceList);
        scanBtn = (Button)root.findViewById(R.id.scanBtn);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        scanBtn.setEnabled(true);

        root.findViewById(R.id.scanBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(isWifiConnected()){
                                findSubnetDevices();
                            } else{
                                // Use the Builder class for convenient dialog construction
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Info");
                                builder.setMessage("No wifi connection available");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        getActivity().onBackPressed();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                EspDevice espDevice = new EspDevice();
                espDevice.setDeviceHostname(deviceList.get(position).getDeviceHostname());
                espDevice.setDeviceIp(deviceList.get(position).getDeviceIp());
                espDevice.setDeviceMac(deviceList.get(position).getDeviceMac());

                userSettings.saveSelectedDevice(espDevice);

                showAlertDialog();
            }
        });

        return root;
    }

    private void showAlertDialog(){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Info");
        builder.setMessage("Your device is selected!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                progressBar.setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setEnabled(final View view, final boolean enabled) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.setEnabled(enabled);
                }
            }
        });
    }

    private boolean isWifiConnected(){
        WifiManager wifiMgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiMgr.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if( !mWifi.isConnected()){
                return false; // Not connected to an access point
            } else {
                return  true;
            }
        } else {
            return false;
        }
    }

    private void findSubnetDevices() {

        try{
            setEnabled(scanBtn, false);

            deviceList = new ArrayList<EspDevice>();
            listOfDevices = new ArrayList<>();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });

            SubnetDevices subnetDevices = SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
                @Override
                public void onDeviceFound(Device device) {
                    foundDevice = new EspDevice();
                    foundDevice.setDeviceIp(device.ip);
                    foundDevice.setDeviceMac(device.mac);
                    foundDevice.setDeviceHostname(device.hostname);

                    String mac = device.mac;

                    try{
                        if(mac != null){
                            if(mac.contains(MAC_HEADER)){
                                deviceList.add(foundDevice);
                            }
                        }

                    } catch(Exception ex){
                        ex.printStackTrace();
                    }

                }

                @Override
                public void onFinished(final ArrayList<Device> devicesFound) {

                    try{
                        deviceAdapter = new DeviceAdapter(deviceList, context);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                deviceListView.setAdapter(deviceAdapter);
                                listOfDevices = devicesFound;
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }

                }

            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scanBtn.setEnabled(true);
                }
            });
        } catch (Exception ex){
            ex.printStackTrace();
        }



        // Below is example of how to cancel a running scan
        // subnetDevices.cancel();

    }


}