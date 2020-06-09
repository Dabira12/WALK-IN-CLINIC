package com.example.finalproj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ClinicCustomAdapter extends ArrayAdapter {

    private final Context context;
    private final String[] clinics;

    public ClinicCustomAdapter(Context context, String[] clinics){
        super(context, R.layout.cliniclist, clinics);
        this.context = context;
        this.clinics = clinics;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.cliniclist, parent, false);

        TextView clinicNameTextField = (TextView) rowView.findViewById(R.id.clinicName);
        TextView clinicAddressField = (TextView) rowView.findViewById(R.id.clinicAddress);

        clinicNameTextField.setText(clinics[position]);
        clinicAddressField.setText(clinics[position] + " test field");

        return rowView;
    }

}
