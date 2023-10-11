package com.ads.uvlamp.Timer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.ads.uvlamp.Timer.sampledata.AlarmRepository;
import com.ads.uvlamp.Timer.sampledata.TimerWeek;


public class CreateTimerViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;

    public CreateTimerViewModel(@NonNull Application application) {
        super(application);

        alarmRepository = new AlarmRepository(application);
    }

    public void insert(TimerWeek timerWeek) {
        alarmRepository.insert(timerWeek);
    }
}
