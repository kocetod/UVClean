package com.ads.uvlamp;

import android.content.Context;
import android.content.SharedPreferences;


public class UserSettings {

    private static final String MY_PREFS_NAME = "userSettings";

    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    Context context;

    private static String IP_ADDRESS = "ipAddress";
    private static String HOSTNAME = "hostname";
    private static String MAC_ADDRESS = "macAddress";
    private static String DEVICE_ID = "deviceId";
    private static String CURRENT_WIFI_NEWORK = "currentWifi";
    private static String CURRENT_VERSION_FIRMWATE = "currentFirmware";


    public UserSettings(Context _context){
        context = _context;
        editor = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSelectedDevice(EspDevice espDevice){
        try{
            editor.putString(IP_ADDRESS, espDevice.getDeviceIp());
            editor.putString(HOSTNAME, espDevice.getDeviceHostname());
            editor.putString(MAC_ADDRESS, espDevice.getDeviceMac());
            editor.putString(DEVICE_ID, espDevice.getDeviceId());
            editor.putString(CURRENT_VERSION_FIRMWATE, espDevice.getCurrentFirmware());
            editor.putString(CURRENT_WIFI_NEWORK, espDevice.getCurrentSsid());

            editor.apply();
        } catch (Exception ex){

        }
    }

    public void saveWorkTime(String workTime){
        try{
            editor.putString("worktime", workTime);

            editor.apply();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public String getWorkTime(){
        String workTime = prefs.getString("worktime", "");

        return workTime;
    }


    public EspDevice getEspDevice(){

        String ipAddress = prefs.getString(IP_ADDRESS, "");
        String hostname = prefs.getString(HOSTNAME, "");
        String macAddress = prefs.getString(MAC_ADDRESS, "");
        String deviceId = prefs.getString(DEVICE_ID, "");
        String currentWifiNetwork = prefs.getString(CURRENT_WIFI_NEWORK, "");
        String currentFirmware = prefs.getString(CURRENT_VERSION_FIRMWATE, "");

        EspDevice espDevice = new EspDevice();

        espDevice.setDeviceIp(ipAddress);
        espDevice.setDeviceHostname(hostname);
        espDevice.setDeviceMac(macAddress);
        espDevice.setDeviceId(deviceId);
        espDevice.setCurrentFirmware(currentFirmware);
        espDevice.setCurrentSsid(currentWifiNetwork);

        return espDevice;
    }

}
