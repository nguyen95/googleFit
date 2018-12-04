package com.example.trungnguyen.healthcare2.main;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trungnguyen.healthcare2.R;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getTimeInstance;

public class FitAdapter extends RecyclerView.Adapter<FitAdapter.RecyclerViewHolder> {
    private static final String TAG = "FIT_TEST";
    private ArrayList<DataSet> data = new ArrayList<>();

    public FitAdapter(ArrayList<DataSet> data) {
        this.data = data;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int i) {
        DataSet dataSet = data.get(i);
        String s = "";
        s += "Data returned for Data type: " + dataSet.getDataType().getName() + "\n";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");

        for (DataPoint dp : dataSet.getDataPoints()) {
            s += "Data point:" + "\n";
            s += "\tType: " + dp.getDataType().getName() + "\n";
            s += "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
            s += "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));

            boolean isSleep = false;
            for (Field field : dp.getDataType().getFields()) {
                s += "\tField: " + field.getName() + "  -  Value: " + dp.getValue(field) + "\n";
                if(dp.getValue(field).toString().equals("72")){
                    isSleep = true;
                }
                if(field.getName().contains("duration") && isSleep){
                    Value value = dp.getValue(field);
                    float sleepHours  = (float) (Math.round((value.asInt() * 2.778 * 0.0000001*10.0))/10.0);
                    s += "\tField: Sleep duration in h " + sleepHours + "\n";
                }
            }
        }
        holder.txtInfoFit.setText(s);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView txtInfoFit;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            itemView.setBackgroundColor(Color.WHITE);
            txtInfoFit = itemView.findViewById(R.id.txt_fit);
        }
    }
}

