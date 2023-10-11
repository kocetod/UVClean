package com.ads.uvlamp.Timer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.ads.uvlamp.Timer.sampledata.AlarmRepository;
import com.ads.uvlamp.Timer.sampledata.TimerWeek;

import java.util.List;

public class TimerListViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;
    private LiveData<List<TimerWeek>> alarmsLiveData;

    public TimerListViewModel(@NonNull Application application) {
        super(application);

        try{
            alarmRepository = new AlarmRepository(application);
            alarmsLiveData = alarmRepository.getAlarmsLiveData();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void update(TimerWeek timerWeek) {
        alarmRepository.update(timerWeek);
    }

    public LiveData<List<TimerWeek>> getAlarmsLiveData() {
        return alarmsLiveData;
    }
}
