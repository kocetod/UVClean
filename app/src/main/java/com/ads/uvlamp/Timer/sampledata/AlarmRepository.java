package com.ads.uvlamp.Timer.sampledata;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<TimerWeek>> alarmsLiveData;

    public AlarmRepository(Application application) {

        AlarmDatabase db = AlarmDatabase.getDatabase(application);
        alarmDao = db.alarmDao();
        alarmsLiveData = alarmDao.getAlarms();
    }

    public void insert(final TimerWeek timerWeek) {
        AlarmDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                alarmDao.insert(timerWeek);
            }
        });
    }

    public void update(final TimerWeek timerWeek) {
        AlarmDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                alarmDao.update(timerWeek);
            }
        });
    }

    public LiveData<List<TimerWeek>> getAlarmsLiveData() {
        return alarmsLiveData;
    }
}
