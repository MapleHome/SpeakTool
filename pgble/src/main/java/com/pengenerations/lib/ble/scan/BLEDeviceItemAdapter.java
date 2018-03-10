package com.pengenerations.lib.ble.scan;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pgbleex.R;

import java.util.ArrayList;

public class BLEDeviceItemAdapter extends ArrayAdapter<BluetoothDevice> {
    public ArrayList<BluetoothDevice> m_items = null;

    public BLEDeviceItemAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        m_items = new ArrayList<BluetoothDevice>();
    }

    public void addItem(BluetoothDevice dev) {
        if (m_items.contains(dev) == false) {
            m_items.add(dev);
            notifyDataSetChanged();
        }
    }

    public void Clear() {
        if (m_items != null)
            m_items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return m_items.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return m_items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.devicelist_item, null);

            holder.name = (TextView) v.findViewById(R.id.DeviceName);
            holder.address = (TextView) v.findViewById(R.id.DeviceAddress);

            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        BluetoothDevice item = m_items.get(position);
        if (item == null) {
            holder.name.setText("");
            holder.address.setText("");
        } else {
            holder.name.setText(item.getName());
            holder.address.setText(item.getAddress());
        }
        return v;
    }

    public class Holder {
        TextView name;
        TextView address;
    }
}
