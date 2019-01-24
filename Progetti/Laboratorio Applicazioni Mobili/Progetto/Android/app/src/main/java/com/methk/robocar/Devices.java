package com.methk.robocar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class Devices extends AppCompatActivity {

    // Widgets
    private ListView devicesList;

    // Bluetooth
    private BluetoothAdapter bluetoothAdapter = null;
    private Set<BluetoothDevice> pairedDevices;
    private String MACAddress;
    protected static String EXTRA_ADDRESS = "MacAddress";
    protected static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } // Restart this activity if bluetooth is disconnected
        }, new IntentFilter(BluetoothConnection.INTENT_FILTER_STOP));

        FloatingActionButton fab = findViewById(R.id.fab_refresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pairedDevicesList();
            }
        });

        // Initialize widgets
        devicesList = findViewById(R.id.paired_dev_listview);

        // Initialize bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pairedDevicesList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK)
            pairedDevicesList();
    }

    // List all paired bluetooth devices
    private void pairedDevicesList() {
        if(bluetoothAdapter == null) { // This device has not bluetooth adapter
            Toast.makeText(getApplicationContext(), getString(R.string.devices_bt_not_avail), Toast.LENGTH_LONG).show();
            finish();
        } else if(!bluetoothAdapter.isEnabled()) { // This device has bluetooth adapter but turned off
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1); // Intent to turn on bluetooth adapter
        } else {
            pairedDevices = bluetoothAdapter.getBondedDevices();
            ArrayList<String> list = new ArrayList<>(); // ArrayList with name and MAC address of paired devices

            if (pairedDevices.size() > 0)
                for(BluetoothDevice bt : pairedDevices)
                    list.add(bt.getName() + "\n" + bt.getAddress());
            else
                Toast.makeText(getApplicationContext(), getString(R.string.devices_dev_not_found), Toast.LENGTH_LONG).show();

            // Display paired devices in the listview
            devicesList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, list));
            devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3) {
                    // MAC address are last 17 characters of the textview clicked
                    String info = ((TextView) v).getText().toString();
                    MACAddress = info.substring(info.length() - 17);
                    new StartService().execute(); // AsyncTask to start the bluetooth service
                }
            });
        }
    }

    private class StartService extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Devices.this, getString(R.string.devices_dialog_title), getString(R.string.devices_dialog_content));  // Loading dialog
        }

        @Override
        protected Void doInBackground(Void... devices) {
            startService(new Intent(Devices.this, BluetoothConnection.class).putExtra(EXTRA_ADDRESS, MACAddress));
            return null;
        }
    }

}
