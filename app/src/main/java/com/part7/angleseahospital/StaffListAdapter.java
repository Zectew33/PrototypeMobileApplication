package com.part7.angleseahospital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

public class StaffListAdapter extends ArrayAdapter<Staff> {
    private static final String TAG = "StaffListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    //Holds variable in a view
    static class ViewHolder {
        TextView id;
        TextView name;
        TextView Shift;
        TextView Date;
        TextView Note;
    }

    public StaffListAdapter(Context context, int resource, ArrayList<Staff> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the staff information
        String id = getItem(position).getID();
        String name = getItem(position).getName();
        String Shift = getItem(position).getShift();
        String Date = getItem(position).getDate();
        String Note = getItem(position).getNote();

        //Create the staff object with the information
        Staff staff = new Staff(id, name, Shift, Date, Note);

        //Create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false); //It can cause problems if there are a lot of items in the listView
            holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.textView1);
            holder.name = (TextView) convertView.findViewById(R.id.textView2);
            holder.Shift = (TextView) convertView.findViewById(R.id.textView3);
            holder.Date = (TextView) convertView.findViewById(R.id.textView4);
            holder.Note = (TextView) convertView.findViewById(R.id.textView5);

            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down : R.anim.load_up);
        result.startAnimation(animation);
        lastPosition = position;

        holder.id.setText(staff.getID());
        holder.name.setText(staff.getName());
        holder.Shift.setText(staff.getShift());
        holder.Date.setText(staff.getDate());
        holder.Note.setText(staff.getNote());

        return convertView;
    }
}