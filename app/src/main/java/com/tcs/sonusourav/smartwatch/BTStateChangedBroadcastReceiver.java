package com.tcs.sonusourav.smartwatch;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Switch;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by SONU SOURAV on 5/12/2018.
 */


public class BTStateChangedBroadcastReceiver extends BroadcastReceiver {

    Switch BTswitch;


    @Override
    public void onReceive(Context context, Intent intent) {

        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                -1);

        switch (state) {
            case BluetoothAdapter.STATE_CONNECTED:

                Toast.makeText(context,
                        "Bluetooth: STATE_CONNECTED"
                        , LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_CONNECTING:
                Toast.makeText(context,
                        "Bluetooth: STATE_CONNECTING",
                        LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_DISCONNECTED:
                Toast.makeText(context,
                        "Bluetooth: STATE_DISCONNECTED",
                        LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_DISCONNECTING:
                Toast.makeText(context,
                        "Bluetooth: STATE_DISCONNECTING",
                        LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_OFF:
                Toast.makeText(context,
                        "Bluetooth: STATE_OFF",
                        Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_ON:
                Toast.makeText(context,
                        "Bluetooth: STATE_ON",
                        Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                Toast.makeText(context,
                        "Bluetooth: STATE_TURNING_OFF",
                        Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                Toast.makeText(context,
                        "Bluetooth: STATE_TURNING_ON",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

}