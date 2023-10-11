package com.ads.uvlamp.Timer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.uvlamp.R;
import com.ads.uvlamp.Timer.sampledata.TimerWeek;

import java.util.ArrayList;
import java.util.List;


public class TimerRecyclerViewAdapter extends RecyclerView.Adapter<TimerViewHolder> {
    private List<TimerWeek> timerWeeksList;
    private OnToggleAlarmListener listener;

    public TimerRecyclerViewAdapter(OnToggleAlarmListener listener) {
        this.timerWeeksList = new ArrayList<TimerWeek>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_item_timer, parent, false);
        return new TimerViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        TimerWeek timerWeek = timerWeeksList.get(position);
        holder.bind(timerWeek);
    }

    @Override
    public int getItemCount() {
        return timerWeeksList.size();
    }

    public void setAlarms(List<TimerWeek> alarms) {
        this.timerWeeksList = alarms;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull TimerViewHolder holder) {
        super.onViewRecycled(holder);
        holder.alarmStarted.setOnCheckedChangeListener(null);
    }
}

