package com.ads.uvlamp.Timer;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.uvlamp.R;
import com.ads.uvlamp.Timer.sampledata.TimerWeek;


public class TimerViewHolder extends RecyclerView.ViewHolder {
    private TextView alarmTime;
    private ImageView alarmRecurring;
    private TextView alarmRecurringDays;
    private TextView alarmTitle;

    public Switch alarmStarted;

    private OnToggleAlarmListener listener;

    public TimerViewHolder(@NonNull View itemView, OnToggleAlarmListener listener) {
        super(itemView);

        alarmTime = itemView.findViewById(R.id.item_alarm_time);
        alarmStarted = itemView.findViewById(R.id.item_alarm_started);
        alarmRecurring = itemView.findViewById(R.id.item_alarm_recurring);
        alarmRecurringDays = itemView.findViewById(R.id.item_alarm_recurringDays);
        alarmTitle = itemView.findViewById(R.id.item_alarm_title);

        this.listener = listener;
    }

    public void bind(final TimerWeek timerWeek) {
        String alarmText = String.format("%02d:%02d", timerWeek.getHour(), timerWeek.getMinute());

        alarmTime.setText(alarmText);
        alarmStarted.setChecked(timerWeek.isStarted());

        if (timerWeek.isRecurring()) {
            alarmRecurring.setImageResource(R.drawable.ic_repeat_black_24dp);
            alarmRecurringDays.setText(timerWeek.getRecurringDaysText());
        } else {
            alarmRecurring.setImageResource(R.drawable.ic_looks_one_black_24dp);
            alarmRecurringDays.setText("Once Off");
        }

        if (timerWeek.getTitle().length() != 0) {
            alarmTitle.setText(String.format("%s | %d | %d", timerWeek.getTitle(), timerWeek.getAlarmId(), timerWeek.getCreated()));
        } else {
            alarmTitle.setText(String.format("%s | %d | %d", "Alarm", timerWeek.getAlarmId(), timerWeek.getCreated()));
        }

        alarmStarted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onToggle(timerWeek);
            }
        });
    }
}
