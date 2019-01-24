package com.methk.robocar;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Controller extends AppCompatActivity implements View.OnTouchListener {

    // Widgets
    private TextView distanceTw;
    private Menu menu;

    // Commands
    protected static final int FORWARD = 'F';
    protected static final int RIGHT = 'R';
    protected static final int BACKWARD = 'B';
    protected static final int LEFT = 'L';
    protected static final int STOP = 'S';
    private int lastDistance = 100;

    // Notification
    private static NotificationManagerCompat notificationManager;
    protected static final int NOTIFICATION_ID = 1829;
    protected static final String CHANNEL_ID = "RBCR";
    private static int tooCloseCounter = 0;

    // Service
    private ServiceConnection serviceConnection = new BluetoothServiceConnection();
    private BluetoothConnection bluetoothConnectionService;
    private boolean serviceBound;
    protected static boolean isActive; // Check if the activity is active

    // Recording
    protected static SQLiteDatabase recordingsDB;
    private boolean isRecording;
    private ArrayList<Command> commandsBuffer = new ArrayList<>();
    private long lastTime;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Do binding and setup local broadcast receiver
        isActive = true;
        bindService();

        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(new BluetoothBroadcastReceiver(), new IntentFilter(BluetoothConnection.INTENT_FILTER_COMM));
            LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
                @Override public void onReceive(Context context, Intent intent) { finish(); } // Stop activity if bluetooth is disconnected
            }, new IntentFilter(BluetoothConnection.INTENT_FILTER_STOP));
        } catch (SQLException x) {
            Toast.makeText(this, getString(R.string.controller_error_db), Toast.LENGTH_SHORT).show();
        }

        // Open database
        recordingsDB = openOrCreateDatabase("robocar", MODE_PRIVATE,null);
//        recordingsDB.execSQL("DROP TABLE IF EXISTS Recording;");
        recordingsDB.execSQL("CREATE TABLE IF NOT EXISTS Recording(Name VARCHAR(15) PRIMARY KEY, Commands TEXT, Duration FLOAT);");

        // Initialize widgets
        initWidgets();

        // Create notification channel
        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, BluetoothConnection.class));
        unbindService();
        recordingsDB.close();
        isActive = false;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_controller);
        initWidgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_c, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_record:
                isRecording = !isRecording;
                setRecordingIcon();
                if(!isRecording) saveRecording(); // If the user stopped the recording then save in the DB
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            case R.id.action_custom_controller:
                startActivity(new Intent(this, Sensors.class));
                return true;
            case R.id.action_recordings:
                startActivity(new Intent(this, Recordings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(bluetoothConnectionService != null) {
            switch (v.getId()) {
                case R.id.btn_f:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        bluetoothConnectionService.sendCommand(FORWARD);
                        if(isRecording) addCommandToBuffer(FORWARD);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        bluetoothConnectionService.sendCommand(STOP);
                        if(isRecording) addCommandToBuffer(STOP);
                    }
                    break;
                case R.id.btn_r:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        bluetoothConnectionService.sendCommand(RIGHT);
                        if(isRecording) addCommandToBuffer(RIGHT);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        bluetoothConnectionService.sendCommand(STOP);
                        if(isRecording) addCommandToBuffer(STOP);
                    }
                    break;
                case R.id.btn_b:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        bluetoothConnectionService.sendCommand(BACKWARD);
                        if(isRecording) addCommandToBuffer(BACKWARD);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        bluetoothConnectionService.sendCommand(STOP);
                        if(isRecording) addCommandToBuffer(STOP);
                    }
                    break;
                case R.id.btn_l:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        bluetoothConnectionService.sendCommand(LEFT);
                        if(isRecording) addCommandToBuffer(LEFT);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        bluetoothConnectionService.sendCommand(STOP);
                        if(isRecording) addCommandToBuffer(STOP);
                    }
                    break;
                case R.id.btn_reset:
                    clearWarningNotification();
                    break;
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        commandsBuffer.clear();
        isRecording = false;
        setRecordingIcon();
    }

    private void setRecordingIcon() {
        if (menu != null) {
            MenuItem item = menu.findItem(R.id.action_record);
            if(item != null)
                if(isRecording) {
                    lastTime = System.currentTimeMillis();
                    Toast.makeText(this, getString(R.string.controller_rec_on), Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_menu_save);
                } else {
                    Toast.makeText(this, getString(R.string.controller_rec_off), Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_menu_camera);
                }
        }
    }

    private void saveRecording() {
        try {
            if(commandsBuffer.size() > 0) {
                long totalDuration = 0;
                for(Command command : commandsBuffer)
                    totalDuration += command.getDuration();
                recordingsDB.execSQL("INSERT INTO Recording VALUES('" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "', '" + commandsBuffer.toString() + "', " + totalDuration + ");");
            } else
                Toast.makeText(this, getString(R.string.controller_no_command), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.controller_error_saving), Toast.LENGTH_SHORT).show();
        }
        commandsBuffer.clear();
    }

    private void addCommandToBuffer(int command) {
        long actualTime = System.currentTimeMillis();
        commandsBuffer.add(new Command(command, actualTime - lastTime));
        lastTime = actualTime;
    }

    private void bindService() {
        if (bindService(new Intent(Controller.this, BluetoothConnection.class), serviceConnection, Context.BIND_AUTO_CREATE))
            serviceBound = true;
        else
            Toast.makeText(Controller.this, getString(R.string.controller_error_binding), Toast.LENGTH_LONG).show();
    }

    private void unbindService() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    private void initWidgets() {
        findViewById(R.id.btn_f).setOnTouchListener(this);
        findViewById(R.id.btn_l).setOnTouchListener(this);
        findViewById(R.id.btn_b).setOnTouchListener(this);
        findViewById(R.id.btn_r).setOnTouchListener(this);
        findViewById(R.id.btn_reset).setOnTouchListener(this);
        distanceTw = findViewById(R.id.tw_dist);
        distanceTw.setText(getString(R.string.controller_distance, getString(R.string.empty)));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.controller_channel_name);
            String description = getString(R.string.controller_channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setWarningNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.robocar)
                .setContentTitle(getString(R.string.controller_notification_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.controller_notification_content, tooCloseCounter)))
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    protected static void clearWarningNotification() {
        tooCloseCounter = 0;
        notificationManager.cancel(NOTIFICATION_ID);
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

    private class BluetoothBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int dist = intent.getIntExtra("data", 100);

            if(dist > 100) {
                distanceTw.setText(getString(R.string.controller_distance, "> 100 "));
                distanceTw.setTextColor(Color.BLACK);
            } else if(dist <= 100 && dist > 10) {
                distanceTw.setText(getString(R.string.controller_distance, String.valueOf(dist)));
                distanceTw.setTextColor(Color.BLACK);
            } else {
                if(lastDistance > 10) tooCloseCounter++;
                setWarningNotification();
                distanceTw.setText(getString(R.string.controller_distance, String.valueOf(dist)));
                distanceTw.setTextColor(Color.RED);
            }
            lastDistance = dist;
        }
    }

}