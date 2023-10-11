package com.ads.uvlamp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WorkTimeAdapter extends ArrayAdapter<Lamp> implements View.OnClickListener{

    private ArrayList<Lamp> dataSet;
    Context mContext;
    TextView lampNo;
    TextView lampWorktime;
    ImageView icon;

    // View lookup cache
    private static class ViewHolder {

    }

    public WorkTimeAdapter(ArrayList<Lamp> data, Context context) {
        super(context, R.layout.worktime_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Lamp dataModel = (Lamp)object;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.worktime_item, parent, false);

        Lamp dataModel = getItem(position);

        lampNo = (TextView) theView.findViewById(R.id.lamp_no);
        lampWorktime = (TextView) theView.findViewById(R.id.lamp_worktime);
        //esetBtn = (Button) theView.findViewById(R.id.reset_btn);

        icon = (ImageView)theView.findViewById(R.id.icon_lamp);

        lampNo.setText("Bactericidal Lamp No. " + dataModel.getNumber());
        lampWorktime.setText("Work time: " + dataModel.getWorktime() + " h.");

        icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_baseline_av_timer_24));


        return theView;
    }
}