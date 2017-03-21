package com.example.mariya.fortunecookiesapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ItemAdapter extends ArrayAdapter<Contact> {

    private Context context;
    private ArrayList<Contact> items;

    public ItemAdapter(Context context, int resource, ArrayList<Contact> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item, parent, false);
        }

        // Set data into the view.
        TextView idfcookies = (TextView) rowView.findViewById(R.id.idfcookies);
        TextView des = (TextView) rowView.findViewById(R.id.des);

        //get Item
        Contact item = this.items.get(position);

        idfcookies.setText(item.getIdfcookies());
        des.setText(item.getDes());

        return rowView;
    }
}