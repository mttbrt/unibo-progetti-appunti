package com.methk.robocar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Recordings extends AppCompatActivity {

    // Widgets
    private ListView recordingsList;
    private AlertDialog alertDialog;

    // Tools
    private ArrayList<ArrayList<Command>> allRecordings = new ArrayList<>();
    private ExecutionThread execution;
    private boolean stopExecutionThread;
    private String clickedItem;

    // Service
    private ServiceConnection serviceConnection = new BluetoothServiceConnection();
    private BluetoothConnection bluetoothConnectionService;
    private boolean serviceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindService();

        // Initialize widgets
        recordingsList = findViewById(R.id.recordings_listview);

        // Init local broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) { finish(); } // Stop activity if bluetooth is disconnected
        }, new IntentFilter(BluetoothConnection.INTENT_FILTER_STOP));

        // Init list listeners
        recordingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                clickedItem = ((TextView) view).getText().toString().split("\n")[0];
                if(execution == null) {
                    execution = new ExecutionThread();
                    execution.setCommands(allRecordings.get(position));
                    execution.start();
                } else
                    execution.setCommands(allRecordings.get(position));
            }
        });
        recordingsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(alertDialog == null)
                    alertDialog = new AlertDialog.Builder(Recordings.this).create();

                clickedItem = ((TextView) view).getText().toString().split("\n")[0];
                alertDialog.setTitle(getString(R.string.recordings_dialog_title));
                alertDialog.setMessage(getString(R.string.recordings_dialog_content, clickedItem));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.recordings_dialog_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Controller.recordingsDB.execSQL("DELETE FROM Recording WHERE Name = '" + clickedItem + "'");
                                listRecordings();
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.recordings_dialog_negative),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true; // To distinguish between long click and short click
            }
        });

        listRecordings();
    }

    private void listRecordings() {
        ArrayList<String> recordings = new ArrayList<>(); // ArrayList with the rows of the SQLite table

        Cursor cursor = Controller.recordingsDB.rawQuery("SELECT * FROM Recording",null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);
                String commands = cursor.getString(1);
                long duration = Long.parseLong(cursor.getString(2));

                String[] commandsSplit = commands.substring(1, commands.length() - 1).split(",");
                ArrayList<Command> commandsArray = new ArrayList<>();

                for (int i = 0; i < commandsSplit.length; i++) {
                    String[] command = commandsSplit[i].trim().split(":");
                    commandsArray.add(new Command(Integer.parseInt(command[0]), Long.parseLong(command[1])));
                }

                recordings.add(name + "\n " + getString(R.string.recordings_tot_duration, (duration / 1000)));
                allRecordings.add(commandsArray);
                cursor.moveToNext();
            }
        }

        Collections.reverse(recordings);
        recordingsList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, recordings));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothConnectionService.sendCommand(Controller.STOP);
        unbindService();
        stopExecutionThread = true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void bindService() {
        if (bindService(new Intent(Recordings.this, BluetoothConnection.class), serviceConnection, Context.BIND_AUTO_CREATE))
            serviceBound = true;
        else
            Toast.makeText(Recordings.this, getString(R.string.controller_error_binding), Toast.LENGTH_LONG).show();
    }

    private void unbindService() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    private class BluetoothServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothConnectionService = ((BluetoothConnection.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothConnectionService = null;
        }
    }

    private class ExecutionThread extends Thread {
        private ArrayList<Command> commands;
        private int executionPosition = 0;

        public void setCommands(ArrayList<Command> commands) {
            this.commands = commands;
            executionPosition = 0;
        }

        public void run() {
            while(!stopExecutionThread) {
                if(commands != null) {
                    if(executionPosition < commands.size()) {
                        try {
                            bluetoothConnectionService.sendCommand(commands.get(executionPosition).getCommand());
                            this.sleep(commands.get(executionPosition).getDuration());
                            executionPosition++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bluetoothConnectionService.sendCommand(Controller.STOP);
                        commands = null;
                        Recordings.this.runOnUiThread(new Runnable() { public void run() { Toast.makeText(Recordings.this, getString(R.string.recordings_completed), Toast.LENGTH_SHORT).show(); } });
                    }
                }
            }
        }
    }
}
