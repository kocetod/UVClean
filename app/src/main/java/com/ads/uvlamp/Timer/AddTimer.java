package com.ads.uvlamp.Timer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.ads.uvlamp.R;
import com.ads.uvlamp.Timer.sampledata.TimerWeek;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddTimer extends Fragment {

    @BindView(R.id.fragment_createalarm_timePicker)
    TimePicker timePicker;
    @BindView(R.id.fragment_createalarm_title)
    EditText title;
    @BindView(R.id.fragment_createalarm_scheduleAlarm)
    Button scheduleAlarm;
    @BindView(R.id.fragment_createalarm_recurring)
    CheckBox recurring;
    @BindView(R.id.fragment_createalarm_checkMon) CheckBox mon;
    @BindView(R.id.fragment_createalarm_checkTue) CheckBox tue;
    @BindView(R.id.fragment_createalarm_checkWed) CheckBox wed;
    @BindView(R.id.fragment_createalarm_checkThu) CheckBox thu;
    @BindView(R.id.fragment_createalarm_checkFri) CheckBox fri;
    @BindView(R.id.fragment_createalarm_checkSat) CheckBox sat;
    @BindView(R.id.fragment_createalarm_checkSun) CheckBox sun;
    @BindView(R.id.fragment_createalarm_recurring_options)
    LinearLayout recurringOptions;

    private CreateTimerViewModel createTimerViewModel;

    public AddTimer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTimerViewModel = ViewModelProviders.of(this).get(CreateTimerViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_timer, container, false);

        ButterKnife.bind(this, view);

        recurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recurringOptions.setVisibility(View.VISIBLE);
                } else {
                    recurringOptions.setVisibility(View.GONE);
                }
            }
        });

        scheduleAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAlarm();
                Navigation.findNavController(v).navigate(R.id.open_timers_list_fragment);
            }
        });

        return view;
    }

    private void scheduleAlarm() {
        int alarmId = new Random().nextInt(Integer.MAX_VALUE);

        TimerWeek timerWeek = new TimerWeek(
                alarmId,
                TimePickerUtil.getTimePickerHour(timePicker),
                TimePickerUtil.getTimePickerMinute(timePicker),
                title.getText().toString(),
                System.currentTimeMillis(),
                true,
                recurring.isChecked(),
                mon.isChecked(),
                tue.isChecked(),
                wed.isChecked(),
                thu.isChecked(),
                fri.isChecked(),
                sat.isChecked(),
                sun.isChecked()
        );
        createTimerViewModel.insert(timerWeek);

    }
}