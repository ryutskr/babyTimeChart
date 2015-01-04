package com.babytimechart.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.activity.babytimechart.R;
import com.babytimechart.utils.Utils;

public class BabyTimeSpinnerAdapter extends ArrayAdapter<String> {


    public BabyTimeSpinnerAdapter(Context context) {
        super(context, 0);
    }

    public void addItem(String string) {
        add(string);
    }

    public class ViewHolder {
        public final TextView row_top;
        public final TextView row_bottom;

        public ViewHolder(TextView top, TextView bottom) {
            this.row_top = top;
            this.row_bottom = bottom;
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomDropDownView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        String item = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_spinner_row, null);

            TextView title = (TextView) view.findViewById(R.id.row_top);
            TextView title_explain = (TextView) view.findViewById(R.id.row_bottom);

            title.setText(new Utils().countDays(getContext(), item));
            title_explain.setText(item);

            view.setTag(new ViewHolder(title, title_explain));
        } else {
            ViewHolder viewHolderRow = (ViewHolder) view.getTag();
            viewHolderRow.row_top.setText(new Utils().countDays(getContext(), item));
            viewHolderRow.row_bottom.setText(item);
        }
        return view;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        String item = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_spinner_row, null);

            TextView title = (TextView) view.findViewById(R.id.row_top);
            TextView title_explain = (TextView) view.findViewById(R.id.row_bottom);

            title.setText(new Utils().countDays(getContext(), item));
            title_explain.setVisibility(View.GONE);

            view.setTag(new ViewHolder(title, title_explain));
        } else {
            ViewHolder viewHolderRow = (ViewHolder) view.getTag();
            viewHolderRow.row_top.setText(new Utils().countDays(getContext(), item));
        }
        return view;
    }

}