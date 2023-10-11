package com.ads.uvlamp.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.ads.uvlamp.CheckService;
import com.ads.uvlamp.Cmd;
import com.ads.uvlamp.EspDevice;
import com.ads.uvlamp.Timer.TimerActivity;
import com.ads.uvlamp.ui.HTTPConnection;
import com.stealthcopter.networktools.Ping;
import androidx.lifecycle.ViewModelProviders;
import com.ads.uvlamp.R;
import com.ads.uvlamp.UserSettings;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView label_start, label_stop, status, networkStatus, temp_text, timeLeftStartText, timeLeftStopText, alarm;
    private Spinner select_start, select_stop;
    private ToggleButton switchDevice;
    private Thread request;
    private Button prepareBtn;
    private ImageView deviceImage;

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

    final int DELAY_UPDATE = 1000 * 1 * 15;

    public String WORK_WOUR;
    private int watchdog;

    private Thread networkThread;
    private Thread postThread;

    private ConstraintLayout timerLayout, infoLayout;

    private boolean flagIsWork;
    private boolean headerIsRead;

    String timeForStop;
    String timeForStart;

    private HTTPConnection httpConnection;
    private final OkHttpClient client = new OkHttpClient();

    private UserSettings userSettings;
    private Cmd cmd;
    private Button closeTimerBtn, openTimerBtn, openInfoBtn, closeInfoBtn, weekTimer;

    HashMap<String, String> responseHedersHash;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        userSettings = new UserSettings(getContext());
        httpConnection = new HTTPConnection();

        implementView(root);

        headerIsRead = false;

        openInfoBtn.setVisibility(View.GONE);
        deviceImage.setVisibility(View.GONE);

        uiEnable(false, false);
        changeNetworkStatus(false);

        pingDevice(userSettings.getEspDevice().getDeviceIp());

        selectTimers();

        getDeviceStatus();

        prepareBtnClick();

        switchDeviceBtnClick();

        periodUpdate();

        initButtons();

        status.setText("Work status:");

        initStatus();



        return root;
    }

    private void getData(){

        EspDevice espDevice = new EspDevice();

        espDevice.setDeviceIp(userSettings.getEspDevice().getDeviceIp());
        espDevice.setDeviceMac(userSettings.getEspDevice().getDeviceMac());
        espDevice.setDeviceHostname(userSettings.getEspDevice().getDeviceHostname());
        espDevice.setDeviceNum(userSettings.getEspDevice().getDeviceNum());
        espDevice.setCurrentSsid(responseHedersHash.get(HEADER_SSID));
        espDevice.setCurrentFirmware(responseHedersHash.get(HEADER_FIRMWARE_VER));
        espDevice.setDeviceId(responseHedersHash.get(HEADER_ID));

        userSettings.saveSelectedDevice(espDevice);
    }

    private void implementView(View root){
        try{
            deviceImage = root.findViewById(R.id.device_image);

            closeTimerBtn = root.findViewById(R.id.button_close_timer);
            openTimerBtn = root.findViewById(R.id.button_open_timer);

            weekTimer = root.findViewById(R.id.button_open_week);

            label_start = root.findViewById(R.id.label_1);
            label_stop = root.findViewById(R.id.label_2);
            status = root.findViewById(R.id.status_bar);
            networkStatus = root.findViewById(R.id.network_status);
            temp_text = root.findViewById(R.id.temp_text);

            status = root.findViewById(R.id.status_bar);
            timeLeftStopText = root.findViewById(R.id.stop_after);
            timeLeftStartText = root.findViewById(R.id.start_after);
            alarm = root.findViewById(R.id.alarm);

            select_start = (Spinner) root.findViewById(R.id.select_start);
            select_stop = (Spinner) root.findViewById(R.id.select_stop);

            switchDevice = root.findViewById(R.id.start_btn);
            prepareBtn = root.findViewById(R.id.prepare_btn);

            openInfoBtn = root.findViewById(R.id.button_open_info);
            closeInfoBtn = root.findViewById(R.id.button_close_info);

            timerLayout = (ConstraintLayout) root.findViewById(R.id.include_timerLayout);
            infoLayout = (ConstraintLayout) root.findViewById(R.id.include_infoLayout);

            timerLayout.setVisibility(View.GONE);
            infoLayout.setVisibility(View.GONE);

        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void openWeekTimer(){
        weekTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initButtons(){
        openTimer();
        closeTimer();

        openInfo();
        closeInfo();

        openWeekTimer();
    }

    private void openInfo(){

        openInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerLayout.getVisibility() == View.GONE) {
                    infoLayout.setVisibility(View.VISIBLE);
                    switchDevice.setVisibility(View.GONE);
                    status.setVisibility(View.GONE);
                    timeLeftStartText.setVisibility(View.GONE);
                    timeLeftStopText.setVisibility(View.GONE);
                    alarm.setVisibility(View.GONE);
                }
            }
        });
    }

    private void closeInfo(){
        closeInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoLayout.setVisibility(View.GONE);
                switchDevice.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
                timeLeftStartText.setVisibility(View.VISIBLE);
                timeLeftStopText.setVisibility(View.VISIBLE);
                alarm.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openTimer(){
        openTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerLayout.setVisibility(View.VISIBLE);
                switchDevice.setVisibility(View.GONE);
                status.setVisibility(View.GONE);
                timeLeftStartText.setVisibility(View.GONE);
                timeLeftStopText.setVisibility(View.GONE);
                alarm.setVisibility(View.GONE);
            }
        });
    }

    private void closeTimer(){
        closeTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerLayout.setVisibility(View.GONE);
                switchDevice.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
                timeLeftStartText.setVisibility(View.VISIBLE);
                timeLeftStopText.setVisibility(View.VISIBLE);
                alarm.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getDeviceStatus(){

        final String ipHeader = "http://" + userSettings.getEspDevice().getDeviceIp();

        postAndGet(ipHeader + Cmd.GET);
    }

    private void uiControl(){

        if(responseHedersHash.get("Status").equals("work")){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    flagIsWork = true;
                    switchDevice.setChecked(false);

                    uiEnable(false, true);
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    flagIsWork = false;
                    switchDevice.setChecked(true);
                    uiEnable(true, true);
                }
            });
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temp_text.setText("Temp: " + responseHedersHash.get("Temperature"));
                status.setText("WORK STATUS: " + responseHedersHash.get("Status"));
                timeLeftStartText.setText("START AFTER: " + responseHedersHash.get("Time left to start") + " min.");
                timeLeftStopText.setText("STOP AFTER: " + responseHedersHash.get("Time left for stop") + " min.");
                alarm.setText(responseHedersHash.get("ALARM: " + "Alarm"));
            }
        });
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
                    responseHedersHash.put(HEADER_TYPE, map.get(HEADER_TYPE).get(0));
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

                    updateStatus();

                    getData();

                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        };

        networkThread.start();
    }

    private void initStatus(){

        try {
            watchDog();

            if (responseHedersHash.size() != 0) {
                uiControl();
                changeNetworkStatus(true);
            } else {

                changeNetworkStatus(false);
                uiEnable(false, false);
                showAlertDialog("Error", "Device is not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareBtnClick(){
        prepareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_start.getSelectedItemPosition() != 0 && select_stop.getSelectedItemPosition() != 0) {
                    try {
                        if(!flagIsWork){
                            postAndGet(timeForStart);
                            Thread.sleep(50);
                            postAndGet(timeForStop);

                            getDeviceStatus();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showAlertDialog("Warning", "Please, select turn on and turn off timer!");
                }
            }
        });
    }

    private void switchDeviceBtnClick(){

        final String ipHeader = "http://" + userSettings.getEspDevice().getDeviceIp();

        switchDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (flagIsWork) {
                        postAndGet(ipHeader + Cmd.STOP_NOW);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flagIsWork = false;
                                switchDevice.setChecked(true);
                                uiEnable(true, true);
                            }
                        });

                    } else {
                        postAndGet(ipHeader + Cmd.START_IMMEDIATELY);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flagIsWork = true;
                                switchDevice.setChecked(false);
                                uiEnable(false, true);
                            }
                        });
                    }
                    //updateStatus();
                    periodUpdate();

                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    private void showAlertDialog(String header, String message){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(header);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectTimers(){

        final String ipHeader = "http://" + userSettings.getEspDevice().getDeviceIp();

        select_start.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        timeForStart = (ipHeader + cmd.START_NOW);
                        break;
                    case 2:
                        timeForStart = (ipHeader + cmd.START_AFTER_1H);
                        break;
                    case 3:
                        timeForStart = (ipHeader + cmd.START_AFTER_2H);
                        break;
                    case 4:
                        timeForStart = (ipHeader + cmd.START_AFTER_3H);
                        break;
                    case 5:
                        timeForStart = (ipHeader + cmd.START_AFTER_4H);
                        break;
                    case 6:
                        timeForStart = (ipHeader + cmd.START_AFTER_5H);
                        break;
                    case 7:
                        timeForStart = (ipHeader + cmd.START_AFTER_6H);
                        break;
                    case 8:
                        timeForStart = (ipHeader + cmd.START_AFTER_7H);
                        break;
                    case 9:
                        timeForStart = (ipHeader + cmd.START_AFTER_8H);
                        break;
                    case 10:
                        timeForStart = (ipHeader + cmd.START_AFTER_9H);
                        break;
                    case 11:
                        timeForStart = (ipHeader + cmd.START_AFTER_10H);
                        break;
                    case 12:
                        timeForStart = (ipHeader + cmd.START_AFTER_11H);
                        break;
                    case 13:
                        timeForStart = (ipHeader + cmd.START_AFTER_12H);
                        break;
                    case 14:
                        timeForStart = (ipHeader + cmd.START_AFTER_13H);
                        break;
                    case 15:
                        timeForStart = (ipHeader + cmd.START_AFTER_14H);
                        break;
                    case 16:
                        timeForStart = (ipHeader + cmd.START_AFTER_15H);
                        break;
                    case 17:
                        timeForStart = (ipHeader + cmd.START_AFTER_16H);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        select_stop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_1H);
                        break;
                    case 2:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_2H);
                        break;
                    case 3:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_3H);
                        break;
                    case 4:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_4H);
                        break;
                    case 5:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_5H);
                        break;
                    case 6:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_6H);
                        break;
                    case 7:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_7H);
                        break;
                    case 8:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_8H);
                        break;
                    case 9:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_9H);
                        break;
                    case 10:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_10H);
                        break;
                    case 11:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_11H);
                        break;
                    case 12:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_12H);
                        break;
                    case 13:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_13H);
                        break;
                    case 14:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_14H);
                        break;
                    case 15:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_15H);
                        break;
                    case 16:
                        timeForStop = (ipHeader + cmd.STOP_AFTER_16H);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void watchDog() {
        int waitCount = 15;

         while (waitCount != 0){
             if(responseHedersHash.size() != 7){
                 waitCount --;
                 try {
                     Thread.sleep(500);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             } else {
                 break;
             }

         }
    }

    private void updateStatus(){

       try {
           if(responseHedersHash.size() != 0){
                uiControl();
                userSettings.saveWorkTime(responseHedersHash.get("Work Time"));
                deviceImage.setVisibility(View.GONE);
           } else {
                changeNetworkStatus(false);
                uiEnable(false, false);
                if(responseHedersHash.get(HEADER_TYPE) == "singe"){
                    deviceImage.setVisibility(View.VISIBLE);
                }
                deviceImage.setVisibility(View.GONE);
           }

       } catch (Exception e) {
           e.printStackTrace();
       }
   }

    private void changeNetworkStatus(boolean status){
        if(status){
            networkStatus.setText("Online");
            networkStatus.setTextColor(Color.GREEN);
        } else {
            networkStatus.setText("Offline");
            networkStatus.setTextColor(Color.RED);
        }
    }

    private void uiEnable(final boolean status, final boolean switchBool){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                label_start.setEnabled(status);
                label_stop.setEnabled(status);

                select_start.setEnabled(status);
                select_stop.setEnabled(status);

                prepareBtn.setEnabled(status);

                switchDevice.setEnabled(switchBool);
            }
        });
    }

    private void pingDevice(String ipAddress) {

        Ping.onAddress(ipAddress).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
            @Override
            public void onResult(PingResult pingResult) {

            }

            @Override
            public void onFinished(PingStats pingStats) {
                if(pingStats.isReachable()){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uiEnable(true, true);
                            changeNetworkStatus(true);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void periodUpdate(){

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            public void run(){
                try {
                    getDeviceStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, DELAY_UPDATE);
            }
        }, DELAY_UPDATE);
    }

}