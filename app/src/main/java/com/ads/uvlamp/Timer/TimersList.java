package com.ads.uvlamp.Timer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ads.uvlamp.R;
import com.ads.uvlamp.Timer.sampledata.TimerWeek;

import java.util.List;

public class TimersList extends Fragment implements OnToggleAlarmListener {

    private Button addAlarm;
    private TimerRecyclerViewAdapter timerRecyclerViewAdapter;
    private RecyclerView timerRecyclerView;
    private TimerListViewModel timerListViewModel;

    public TimersList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timerRecyclerViewAdapter = new TimerRecyclerViewAdapter(this);
        timerListViewModel = ViewModelProviders.of(this).get(TimerListViewModel.class);
        timerListViewModel.getAlarmsLiveData().observe(this, new Observer<List<TimerWeek>>() {
            @Override
            public void onChanged(List<TimerWeek> timerWeeks) {
                if (timerWeeks != null) {
                    timerRecyclerViewAdapter.setAlarms(timerWeeks);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_timers_list, container, false);

        timerRecyclerView = view.findViewById(R.id.fragment_listtimer_recylerView);
        timerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        timerRecyclerView.setAdapter(timerRecyclerViewAdapter);

        addAlarm = view.findViewById(R.id.fragment_listtimer_addAlarm);
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.open_add_timer_fragment);
            }
        });
        return view;
    }

    @Override
    public void onToggle(TimerWeek timerWeek) {
        if (timerWeek.isStarted()) {
            timerWeek.cancelAlarm(getContext());
            timerListViewModel.update(timerWeek);
        } else {
            timerWeek.schedule(getContext());
            timerListViewModel.update(timerWeek);
        }
    }
}