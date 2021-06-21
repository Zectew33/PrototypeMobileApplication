package com.part7.angleseahospital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HoursListAdapter extends ArrayAdapter<Hours> {
    private Context mContext;
    int mResource;

    public HoursListAdapter(@NonNull @NotNull Context context, int resource, @NonNull @NotNull ArrayList<Hours> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String day = getItem(position).getDay();
        String date = getItem(position).getDate();
        String hours = getItem(position).getHours();

        Hours hour = new Hours(day, date, hours);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView tvday = (TextView) convertView.findViewById(R.id.TV1);
        TextView tvdate = (TextView) convertView.findViewById(R.id.TV2);
        TextView tvhours = (TextView) convertView.findViewById(R.id.TV3);

        tvday.setText(day);
        tvdate.setText(date);
        tvhours.setText(hours);

        return convertView;
    }
}
