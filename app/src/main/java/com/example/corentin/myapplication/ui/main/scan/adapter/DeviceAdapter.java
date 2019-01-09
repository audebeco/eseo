package com.example.corentin.myapplication.ui.main.scan.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.corentin.myapplication.R;
import com.example.corentin.myapplication.data.model.Device;

import java.util.ArrayList;

public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
    public DeviceAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        super(context, 0, devices);
    }
    public String adress;
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvMac =  convertView.findViewById(R.id.tvMac);


        assert device !=  null ;
        if(device.getName() == null){
            //tvName.setText(R.string.bluetooth);
            tvName.setText("");
            tvMac.setText("");
        }
        else{
            tvName.setText(device.getName() + "   ");
            tvMac.setText(device.getAddress());
        }

        // Return the completed view to render on screen
        return convertView;
    }


}
