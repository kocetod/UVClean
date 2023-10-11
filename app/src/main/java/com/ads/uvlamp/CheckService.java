package com.ads.uvlamp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckService extends Service {

    private HashMap<String, String> responseHedersHash;
    private Thread networkThread;
    private UserSettings  userSettings ;
    final int DELAY_UPDATE = 1000 * 1 * 10;


    private String HEADER_STATUS = "Status";
    private String HEADER_FIRMWARE_VER = "Firmware Verison";
    private String HEADER_ALARM = "Alarm";
    private String HEADER_TEMP = "Temperature";
    private String HEADER_TIME_START = "Time left to start";
    private String HEADER_TIME_STOP = "Time left for stop";
    private String HEADER_WORKTIME = "Work Time";
    private String HEADER_SSID = "SSID";
    private String HEADER_MAC = "MAC";
    private String HEADER_ID = "DEVICE ID";
    private String HEADER_TYPE = "Device Type";

    private String currentStatus = "";
    private String currentMotion = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void postAndGet(final String link){
        networkThread = new Thread() {
            @Override
            public void run() {
                try{
                    URL obj = new URL(link);
                    URLConnection conn = obj.openConnection();
                    Map<String, List<String>> map = conn.getHeaderFields();

                    responseHedersHash = new HashMap<>();

                    responseHedersHash.put(HEADER_STATUS, map.get(HEADER_STATUS).get(0));
                    responseHedersHash.put(HEADER_ALARM, map.get(HEADER_ALARM).get(0));
                    responseHedersHash.put(HEADER_FIRMWARE_VER, map.get(HEADER_FIRMWARE_VER).get(0));
                    responseHedersHash.put(HEADER_TEMP, map.get(HEADER_TEMP).get(0));
                    responseHedersHash.put(HEADER_TIME_START, map.get(HEADER_TIME_START).get(0));
                    responseHedersHash.put(HEADER_TIME_STOP, map.get(HEADER_TIME_STOP).get(0));
                    responseHedersHash.put(HEADER_WORKTIME, map.get(HEADER_WORKTIME).get(0));
                    responseHedersHash.put(HEADER_SSID, map.get(HEADER_SSID).get(0));
                    responseHedersHash.put(HEADER_MAC, map.get(HEADER_MAC).get(0));
                    responseHedersHash.put(HEADER_ID, map.get(HEADER_ID).get(0));




                    //Toast.makeText(getApplicationContext(), "Get html headers!", Toast.LENGTH_LONG).show();

                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        };

        networkThread.start();
    }

    public void showNotification(String title, String msg)
    {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.uvclean_logo)
                .setContentTitle(title)
                .setContentText(msg);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());

    }

    private void periodUpdate(){

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            public void run(){
                try {
                    if(userSettings.getEspDevice().getDeviceIp() != ""){
                        final String ipHeader = "http://" + userSettings.getEspDevice().getDeviceIp() +  "/get";
                        postAndGet(ipHeader);

                        while (networkThread.isAlive()){

                        }
                        try {
                            if(!currentStatus.contentEquals(responseHedersHash.get(HEADER_STATUS))){
                                showNotification("Device status change!", "Status " + responseHedersHash.get(HEADER_STATUS));
                            }
                            if(!currentMotion.contentEquals(responseHedersHash.get(HEADER_ALARM))){
                                showNotification("Motion detect!", "Device will stop!");
                            }
                            } catch (Exception ex) {
                            ex.printStackTrace();
                            }


                        currentStatus = responseHedersHash.get(HEADER_STATUS);
                        currentMotion = responseHedersHash.get(HEADER_ALARM);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, DELAY_UPDATE);
            }
        }, DELAY_UPDATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userSettings = new UserSettings(getApplicationContext());
        periodUpdate();
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
    }
}
