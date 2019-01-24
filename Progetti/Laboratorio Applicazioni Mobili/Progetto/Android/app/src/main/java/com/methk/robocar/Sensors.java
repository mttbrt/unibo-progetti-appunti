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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sensors extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    // Widgets
    private TextView distanceTw, forwardSensorTw, rightSensorTw, backwardSensorTw, leftSensorTw;
    private TextView forwardRangeTw, rightRangeTw, backwardRangeTw, leftRangeTw;
    private TextView forwardValueTw, rightValueTw, backwardValueTw, leftValueTw;
    private Menu menu;

    // Service
    private ServiceConnection serviceConnection = new BluetoothServiceConnection();
    private BluetoothConnection bluetoothConnectionService;
    private boolean serviceBound;

    // Preferences
    private int[] settings;
    private String[] ranges;
    private int[] values;
    private SharedPreferences preferences;
    private boolean isListeningSensors; // When true sensors can move the Arduino device, when false the user can calibrate the sensors

    // Sensors
    private Sensor forwardSensor;
    private Sensor rightSensor;
    private Sensor backwardSensor;
    private Sensor leftSensor;
    private List<Sensor> sensorsList;
    private SensorManager sensorManager;

    // Commands [O: min value, 1: max value]
    private double[] fSensorVector;
    private double[] rSensorVector;
    private double[] bSensorVector;
    private double[] lSensorVector;
    private int lastExecutedCommand = -1;

    // Recording
    private boolean isRecording; // When true the commands are recorded and not transmitted to the Arduino device
    private ArrayList<Command> commandsBuffer = new ArrayList<>();
    private long lastTime;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Bind service and setup local broadcast receiver
        bindService();
        LocalBroadcastManager.getInstance(this).registerReceiver(new BluetoothBroadcastReceiver(), new IntentFilter(BluetoothConnection.INTENT_FILTER_COMM));
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) { finish(); } // Stop activity if bluetooth is disconnected
        }, new IntentFilter(BluetoothConnection.INTENT_FILTER_STOP));

        // Setup preferences
        preferences = getSharedPreferences(Settings.PREFERENCES_NAME, MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorsList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Setup min - max vectors
        String[] savedFVector = preferences.getString("minmax0", "10000:0").split(":");
        fSensorVector = new double[2]; fSensorVector[0] = Double.parseDouble(savedFVector[0]); fSensorVector[1] = Double.parseDouble(savedFVector[1]);

        String[] savedRVector = preferences.getString("minmax1", "10000:0").split(":");
        rSensorVector = new double[2]; rSensorVector[0] = Double.parseDouble(savedRVector[0]); rSensorVector[1] = Double.parseDouble(savedRVector[1]);

        String[] savedBVector = preferences.getString("minmax2", "10000:0").split(":");
        bSensorVector = new double[2]; bSensorVector[0] = Double.parseDouble(savedBVector[0]); bSensorVector[1] = Double.parseDouble(savedBVector[1]);

        String[] savedLVector = preferences.getString("minmax3", "10000:0").split(":");
        lSensorVector = new double[2]; lSensorVector[0] = Double.parseDouble(savedLVector[0]); lSensorVector[1] = Double.parseDouble(savedLVector[1]);

        // Init settings
        settings = new int[4]; ranges = new String[4]; values = new int[4];
        for(int i = 0; i < 4; i++) {
            if(preferences.getInt("setting" + i, -1) == -1) { // If a value was not saved then set default settings
                if(setStandardSettings()) break; // If standard sensors are available
            } else
                settings[i] = preferences.getInt("setting" + i, 0);
            ranges[i] = preferences.getString("range" + i, "0:0");
            values[i] = preferences.getInt("value" + i, 0);
        }

        forwardSensor = sensorManager.getDefaultSensor(sensorsList.get(settings[0]).getType());
        sensorManager.registerListener(this, forwardSensor, SensorManager.SENSOR_DELAY_NORMAL);
        rightSensor = sensorManager.getDefaultSensor(sensorsList.get(settings[1]).getType());
        sensorManager.registerListener(this, rightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        backwardSensor = sensorManager.getDefaultSensor(sensorsList.get(settings[2]).getType());
        sensorManager.registerListener(this, backwardSensor, SensorManager.SENSOR_DELAY_NORMAL);
        leftSensor = sensorManager.getDefaultSensor(sensorsList.get(settings[3]).getType());
        sensorManager.registerListener(this, leftSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Initialize widgets
        initWidgets();
    }

    private void initWidgets() {
        setPlayPauseIcon();

        findViewById(R.id.btn_resetS).setOnClickListener(this);
        findViewById(R.id.btn_resetC).setOnClickListener(this);
        distanceTw = findViewById(R.id.tw_distS);

        forwardSensorTw = findViewById(R.id.tw_fS);
        rightSensorTw = findViewById(R.id.tw_rS);
        backwardSensorTw = findViewById(R.id.tw_bS);
        leftSensorTw = findViewById(R.id.tw_lS);

        forwardRangeTw = findViewById(R.id.tw_fvS);
        rightRangeTw = findViewById(R.id.tw_rvS);
        backwardRangeTw = findViewById(R.id.tw_bvS);
        leftRangeTw = findViewById(R.id.tw_lvS);

        forwardValueTw = findViewById(R.id.tw_fpS);
        rightValueTw = findViewById(R.id.tw_rpS);
        backwardValueTw = findViewById(R.id.tw_bpS);
        leftValueTw = findViewById(R.id.tw_lpS);

        updateLabels();
    }

    private void updateLabels() {
        forwardSensorTw.setText(sensorsList.get(settings[0]).getName().length() > 16 ? sensorsList.get(settings[0]).getName().substring(0, 16) + "..." : sensorsList.get(settings[0]).getName());
        rightSensorTw.setText(sensorsList.get(settings[1]).getName().length() > 16 ? sensorsList.get(settings[1]).getName().substring(0, 16) + "..." : sensorsList.get(settings[1]).getName());
        backwardSensorTw.setText(sensorsList.get(settings[2]).getName().length() > 16 ? sensorsList.get(settings[2]).getName().substring(0, 16) + "..." : sensorsList.get(settings[2]).getName());
        leftSensorTw.setText(sensorsList.get(settings[3]).getName().length() > 16 ? sensorsList.get(settings[3]).getName().substring(0, 16) + "..." : sensorsList.get(settings[3]).getName());

        forwardRangeTw.setText(ranges[0].length() > 16 ? ranges[0].substring(0, 16) + "..." : ranges[0]);
        rightRangeTw.setText(ranges[1].length() > 16 ? ranges[1].substring(0, 16) + "..." : ranges[1]);
        backwardRangeTw.setText(ranges[2].length() > 16 ? ranges[2].substring(0, 16) + "..." : ranges[2]);
        leftRangeTw.setText(ranges[3].length() > 16 ? ranges[3].substring(0, 16) + "..." : ranges[3]);

        forwardValueTw.setText(String.valueOf(values[0]));
        rightValueTw.setText(String.valueOf(values[1]));
        backwardValueTw.setText(String.valueOf(values[2]));
        leftValueTw.setText(String.valueOf(values[3]));
    }

    private void setPlayPauseIcon() {
        if (menu != null) {
            MenuItem item = menu.findItem(R.id.action_start_stop);
            if(item != null)
                if(isListeningSensors) {
                    Toast.makeText(this, getString(R.string.sensors_calibration_off), Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_media_pause);
                } else {
                    Toast.makeText(this, getString(R.string.sensors_calibration_on), Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_media_play);
                    bluetoothConnectionService.sendCommand(Controller.STOP);
                }
        }
    }

    private void setRecordingIcon() {
        if (menu != null) {
            MenuItem item = menu.findItem(R.id.action_record);
            if(item != null)
                if(isRecording) {
                    lastTime = System.currentTimeMillis();
                    Toast.makeText(this, getString(R.string.sensors_rec_on), Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_menu_save);
                } else {
                    commandsBuffer.clear();
                    Toast.makeText(this, getString(R.string.sensors_rec_off), Toast.LENGTH_SHORT).show();
                    item.setIcon(android.R.drawable.ic_menu_camera);
                }
        }
    }

    private boolean setStandardSettings() {
        int proximitySensorIndex = -1, gameRotationSensorIndex = -1;

        for(int i = 0; i < sensorsList.size(); i++) { // Search for proximity and rotation sensors
            if(proximitySensorIndex == -1 && sensorsList.get(i).getType() == Sensor.TYPE_PROXIMITY && sensorsList.get(i).getMaximumRange() > 0.0) {
                proximitySensorIndex = i;
            } else if (gameRotationSensorIndex == -1 && sensorsList.get(i).getType() == Sensor.TYPE_GAME_ROTATION_VECTOR && sensorsList.get(i).getMaximumRange() > 0.0) {
                gameRotationSensorIndex = i;
            }
        }

        if(proximitySensorIndex > -1 && gameRotationSensorIndex > -1) {
            settings[0] = proximitySensorIndex;
            ranges[0] = "0.0:0.5";
            values[0] = 0;

            settings[1] = gameRotationSensorIndex;
            ranges[1] = "0.0:0.25";
            values[1] = 0;

            settings[2] = proximitySensorIndex;
            ranges[2] = "0.5:1.0";
            values[2] = 0;

            settings[3] = gameRotationSensorIndex;
            ranges[3] = "0.75:1.0";
            values[3] = 0;

            updateLabels();

            for (int i = 0; i < 4; i++) { // Save preferences
                if (preferences.getInt("setting" + i, -1) != settings[i]) { // Save sensors selected (if changed)
                    preferences.edit().putInt("setting" + i, settings[i]).apply();
                    preferences.edit().putBoolean("changedSensor" + i, true).apply(); // The sensor has been changed
                }
                if (!preferences.getString("range" + i, "").equals(ranges[i])) // Save range (if changed)
                    preferences.edit().putString("range" + i, ranges[i]).apply();
                if (preferences.getInt("value" + i, -1) != values[i]) // Save value (if changed)
                    preferences.edit().putInt("value" + i, values[i]).apply();
            }
            return true;
        } else {
            Toast.makeText(this, getString(R.string.settings_problem_standard), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        unbindService();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService();
        isListeningSensors = false;
        setPlayPauseIcon();
        isRecording = false;
        setRecordingIcon();

        // If forward sensor is changed reset sensor's values and change sensor to listen to
        if(preferences.getBoolean("changedSensor0", true)) {
            if(!preferences.getString("minmax0", "-1:-1").equals(fSensorVector[0] + ":" + fSensorVector[1])) // Save value if changed
                preferences.edit().putString("minmax0", "10000:0").apply();
            fSensorVector[0] = 10000; fSensorVector[1] = 0;
            preferences.edit().putBoolean("changedSensor0", false).apply();
            settings[0] = preferences.getInt("setting0", 0);

            Sensor oldSensor = forwardSensor;
            forwardSensor = sensorManager.getDefaultSensor(sensorsList.get(settings[0]).getType());
            if(oldSensor != forwardSensor && oldSensor != rightSensor && oldSensor != backwardSensor && oldSensor != leftSensor) // If the new sensor is not used then unregister listener
                sensorManager.unregisterListener(this, oldSensor);
        }

        // If right sensor is changed reset sensor's values and change sensor to listen to
        if(preferences.getBoolean("changedSensor1", true)) {
            if(!preferences.getString("minmax1", "-1:-1").equals(rSensorVector[0] + ":" + rSensorVector[1])) // Save value if changed
                preferences.edit().putString("minmax1", "10000:0").apply();
            rSensorVector[0] = 10000; rSensorVector[1] = 0;
            preferences.edit().putBoolean("changedSensor1", false).apply();
            settings[1] = preferences.getInt("setting1", 0);

            Sensor oldSensor = rightSensor;
            rightSensor = sensorManager.getDefaultSensor(sensorsList.get(settings[1]).getType());
            if(oldSensor != forwardSensor && oldSensor != rightSensor && oldSensor != backwardSensor && oldSensor != leftSensor) // If the new sensor is not used then unregister listener
                sensorManager.unregisterListener(this, oldSensor);
        }

        // If backward sensor is changed reset sensor's values and change sensor to listen to
        if(preferences.getBoolean("changedSensor2", true)) {
            if(!preferences.getString("minmax2", "-1:-1").equals(bSensorVector[0] + ":" + bSensorVector[1])) // Save value if changed
                preferences.edit().putString("minmax2", "10000:0").apply();
            bSensorVector[0] = 10000; bSensorVector[1] = 0;
            preferences.edit().putBoolean("changedSensor2", false).apply();
            settings[2] = preferences.getInt("setting2", 0);

            Sensor oldSensor = backwardSensor;
            backwardSensor = sensorManager.getDefaultSensor(sensorsList.get(settings[2]).getType());
            if(oldSensor != forwardSensor && oldSensor != rightSensor && oldSensor != backwardSensor && oldSensor != leftSensor) // If the new sensor is not used then unregister listener
                sensorManager.unregisterListener(this, oldSensor);
        }

        // If left sensor is changed reset sensor's values and change sensor to listen to
        if(preferences.getBoolean("changedSensor3", true)) {
            if(!preferences.getString("minmax3", "-1:-1").equals(lSensorVector[0] + ":" + lSensorVector[1])) // Save value if changed
                preferences.edit().putString("minmax3", "10000:0").apply();
            lSensorVector[0] = 10000; lSensorVector[1] = 0;
            preferences.edit().putBoolean("changedSensor3", false).apply();
            settings[3] = preferences.getInt("setting3", 0);

            Sensor oldSensor = leftSensor;
            leftSensor = sensorManager.getDefaultSensor(sensorsList.get(settings[3]).getType());
            if(oldSensor != forwardSensor && oldSensor != rightSensor && oldSensor != backwardSensor && oldSensor != leftSensor) // If the new sensor is not used then unregister listener
                sensorManager.unregisterListener(this, oldSensor);
        }

        for(int i = 0; i < 4; i++) {
            ranges[i] = preferences.getString("range" + i, "0:0");
            values[i] = preferences.getInt("value" + i, 0);
        }

        try {
            sensorManager.registerListener(this, forwardSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, rightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, backwardSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, leftSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.sensors_problem_compat), Toast.LENGTH_LONG).show();
        }

        updateLabels();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService();
        if(bluetoothConnectionService != null) bluetoothConnectionService.sendCommand(Controller.STOP);

        try {
            sensorManager.unregisterListener(this, forwardSensor);
            sensorManager.unregisterListener(this, rightSensor);
            sensorManager.unregisterListener(this, backwardSensor);
            sensorManager.unregisterListener(this, leftSensor);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.sensors_problem_compat), Toast.LENGTH_LONG).show();
        }
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
        setContentView(R.layout.activity_sensors);
        initWidgets();
        lastExecutedCommand = -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_s, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == forwardSensor && values[0] < event.values.length) {
            if(!isListeningSensors) { // If it is in pause, record calibration
                if(event.values[values[0]] < fSensorVector[0])
                    fSensorVector[0] = event.values[values[0]];

                if(event.values[values[0]] > fSensorVector[1])
                    fSensorVector[1] = event.values[values[0]];

                if(!preferences.getString("minmax0", "-1:-1").equals(fSensorVector[0] + ":" + fSensorVector[1])) // Save value if changed
                    preferences.edit().putString("minmax0", fSensorVector[0] + ":" + fSensorVector[1]).apply();
            } else { // If played, don't calibrate and execute commands
                if(bluetoothConnectionService != null) {
                    double lowerPercentage = Double.parseDouble(ranges[0].split(":")[0]);
                    double upperPercentage = Double.parseDouble(ranges[0].split(":")[1]);

                    double lowerBound = fSensorVector[0] + lowerPercentage*(fSensorVector[1] - fSensorVector[0]);
                    double upperBound = fSensorVector[0] + upperPercentage*(fSensorVector[1] - fSensorVector[0]);

                    if(event.values[values[0]] >= lowerBound && event.values[values[0]] <= upperBound) {
                        bluetoothConnectionService.sendCommand(Controller.FORWARD);
                        if(isRecording) addCommandToBuffer(Controller.FORWARD);
                        lastExecutedCommand = 0;
                    } else {
                        if(lastExecutedCommand == 0) {
                            bluetoothConnectionService.sendCommand(Controller.STOP);
                            if(isRecording) addCommandToBuffer(Controller.STOP);
                        }
                    }
                }
            }
        }
        if (event.sensor == rightSensor && values[1] < event.values.length) {
            if(!isListeningSensors) { // If it is in pause, record calibration
                if(event.values[values[1]] < rSensorVector[0])
                    rSensorVector[0] = event.values[values[1]];

                if(event.values[values[1]] > rSensorVector[1])
                    rSensorVector[1] = event.values[values[1]];

                if(!preferences.getString("minmax1", "-1:-1").equals(rSensorVector[0] + ":" + rSensorVector[1])) // Save value if changed
                    preferences.edit().putString("minmax1", rSensorVector[0] + ":" + rSensorVector[1]).apply();
            } else {
                if(bluetoothConnectionService != null) {
                    double lowerPercentage = Double.parseDouble(ranges[1].split(":")[0]);
                    double upperPercentage = Double.parseDouble(ranges[1].split(":")[1]);

                    double lowerBound = rSensorVector[0] + lowerPercentage*(rSensorVector[1] - rSensorVector[0]);
                    double upperBound = rSensorVector[0] + upperPercentage*(rSensorVector[1] - rSensorVector[0]);

                    if(event.values[values[1]] >= lowerBound && event.values[values[1]] <= upperBound) {
                        bluetoothConnectionService.sendCommand(Controller.RIGHT);
                        if(isRecording) addCommandToBuffer(Controller.RIGHT);
                        lastExecutedCommand = 1;
                    } else {
                        if(lastExecutedCommand == 1) {
                            bluetoothConnectionService.sendCommand(Controller.STOP);
                            if(isRecording) addCommandToBuffer(Controller.STOP);
                        }
                    }
                }
            }
        }
        if (event.sensor == backwardSensor && values[2] < event.values.length) {
            if(!isListeningSensors) {
                if(event.values[values[2]] < bSensorVector[0])
                    bSensorVector[0] = event.values[values[2]];

                if(event.values[values[2]] > bSensorVector[1])
                    bSensorVector[1] = event.values[values[2]];

                if(!preferences.getString("minmax2", "-1:-1").equals(bSensorVector[0] + ":" + bSensorVector[1])) // Save value if changed
                    preferences.edit().putString("minmax2", bSensorVector[0] + ":" + bSensorVector[1]).apply();
            } else {
                if(bluetoothConnectionService != null) {
                    double lowerPercentage = Double.parseDouble(ranges[2].split(":")[0]);
                    double upperPercentage = Double.parseDouble(ranges[2].split(":")[1]);

                    double lowerBound = bSensorVector[0] + lowerPercentage*(bSensorVector[1] - bSensorVector[0]);
                    double upperBound = bSensorVector[0] + upperPercentage*(bSensorVector[1] - bSensorVector[0]);

                    if(event.values[values[2]] >= lowerBound && event.values[values[2]] <= upperBound) {
                        bluetoothConnectionService.sendCommand(Controller.BACKWARD);
                        if(isRecording) addCommandToBuffer(Controller.BACKWARD);
                        lastExecutedCommand = 2;
                    } else {
                        if(lastExecutedCommand == 2) {
                            bluetoothConnectionService.sendCommand(Controller.STOP);
                            if(isRecording) addCommandToBuffer(Controller.STOP);
                        }
                    }
                }
            }
        }
        if (event.sensor == leftSensor && values[3] < event.values.length) {
            if(!isListeningSensors) {
                if(event.values[values[3]] < lSensorVector[0])
                    lSensorVector[0] = event.values[values[3]];

                if(event.values[values[3]] > lSensorVector[1])
                    lSensorVector[1] = event.values[values[3]];

                if(!preferences.getString("minmax3", "-1:-1").equals(lSensorVector[0] + ":" + lSensorVector[1])) // Save value if changed
                    preferences.edit().putString("minmax3", lSensorVector[0] + ":" + lSensorVector[1]).apply();

            } else {
                if(bluetoothConnectionService != null) {
                    double lowerPercentage = Double.parseDouble(ranges[3].split(":")[0]);
                    double upperPercentage = Double.parseDouble(ranges[3].split(":")[1]);

                    double lowerBound = lSensorVector[0] + lowerPercentage*(lSensorVector[1] - lSensorVector[0]);
                    double upperBound = lSensorVector[0] + upperPercentage*(lSensorVector[1] - lSensorVector[0]);

                    if(event.values[values[3]] >= lowerBound && event.values[values[3]] <= upperBound) {
                        bluetoothConnectionService.sendCommand(Controller.LEFT);
                        if(isRecording) addCommandToBuffer(Controller.LEFT);
                        lastExecutedCommand = 3;
                    } else {
                        if(lastExecutedCommand == 3) {
                            bluetoothConnectionService.sendCommand(Controller.STOP);
                            if(isRecording) addCommandToBuffer(Controller.STOP);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_start_stop:
                if(isListeningSensors) { // Cannot record if it is calibrating
                    isRecording = false;
                    setRecordingIcon();
                }
                isListeningSensors = !isListeningSensors;
                setPlayPauseIcon();
                return true;
            case R.id.action_record:
                if(isListeningSensors) {
                    if(isRecording)
                        saveRecording(); // If the user stopped the recording then save in the DB
                    isRecording = !isRecording;
                    setRecordingIcon();
                }
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            case R.id.action_custom_controller:
                finish();
                return true;
            case R.id.action_recordings:
                startActivity(new Intent(this, Recordings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_resetS:
                Controller.clearWarningNotification();
                break;
            case R.id.btn_resetC:
                isListeningSensors = false; // Pass to calibration mode
                setPlayPauseIcon();
                isRecording = false; // Stop recording
                setRecordingIcon();
                lastExecutedCommand = -1;

                for(int i = 0; i < 4; i++)
                    preferences.edit().putString("minmax" + i, "10000:0").apply();
                fSensorVector[0] = 10000; fSensorVector[1] = 0;
                rSensorVector[0] = 10000; rSensorVector[1] = 0;
                bSensorVector[0] = 10000; bSensorVector[1] = 0;
                lSensorVector[0] = 10000; lSensorVector[1] = 0;
                break;
        }
    }

    private void saveRecording() {
        try {
            long totalDuration = 0;
            for(Command command : commandsBuffer)
                totalDuration += command.getDuration();
            Controller.recordingsDB.execSQL("INSERT INTO Recording VALUES('" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "', '" + commandsBuffer.toString() + "', " + totalDuration + ");");
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
        if (bindService(new Intent(Sensors.this, BluetoothConnection.class), serviceConnection, Context.BIND_AUTO_CREATE))
            serviceBound = true;
        else
            Toast.makeText(Sensors.this, getString(R.string.controller_error_binding), Toast.LENGTH_LONG).show();
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
                distanceTw.setText(getString(R.string.controller_distance, String.valueOf(dist)));
                distanceTw.setTextColor(Color.RED);
            }
        }
    }

}
