package com.example.myApplication.adapters;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myApplication.R;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] options;

    public MyListAdapter(Activity context, String[] options) {
        super(context, R.layout.profile_options, options);
        this.context=context;
        this.options=options;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.profile_options, null,true);
        TextView titleText = (TextView) rowView.findViewById(R.id.textView_option);
        titleText.setText(options[position]);
        return rowView;

    };
}