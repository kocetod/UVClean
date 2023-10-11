package com.ads.uvlamp.Timer.sampledata;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
    @Insert
    void insert(TimerWeek timerWeek);

    @Query("DELETE FROM alarm_table")
    void deleteAll();

    @Query("SELECT * FROM alarm_table ORDER BY created ASC")
    LiveData<List<TimerWeek>> getAlarms();

    @Update
    void update(TimerWeek timerWeek);
}
