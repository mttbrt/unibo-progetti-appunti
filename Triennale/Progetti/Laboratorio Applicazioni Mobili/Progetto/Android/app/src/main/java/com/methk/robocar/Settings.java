package com.methk.robocar;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    // Widgets
    private TextView forwardSetting, rightSetting, backwardSetting, leftSetting;
    private TextView forwardRange, rightRange, backwardRange, leftRange;
    private TextView forwardValue, rightValue, backwardValue, leftValue;
    private EditText minValue, maxValue, valueChosen;

    // Dialogs
    private Dialog sensorsDialog, rangeDialog, valueDialog;
    private List<Sensor> sensorsList;
    private RadioGroup radioGroup;

    // Settings
    private int[] settings; // Array with the position of each sensor in the sensors list
    private String[] ranges; // Array with the ranges of validity for each sensor
    private int[] values; // Array with the valid axis for each sensor
    private int selectedSetting = 0;

    // Preferences
    protected static final String PREFERENCES_NAME = "sensors";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) { finish(); } // Stop activity if bluetooth is disconnected
        }, new IntentFilter(BluetoothConnection.INTENT_FILTER_STOP));

        // Setup dialogs
        sensorsDialog = new Dialog(this);
        sensorsDialog.setCancelable(false);
        rangeDialog = new Dialog(this);
        rangeDialog.setCancelable(false);
        valueDialog = new Dialog(this);
        valueDialog.setCancelable(false);

        // Initialize widgets
        initWidgets();

        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        try {
            sensorsList = ((SensorManager) getSystemService(SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ALL);
        } catch(NullPointerException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.settings_problem_list), Toast.LENGTH_LONG).show();
            finish();
        }

        // Setup sensors dialog content
        for(int i = 0; i < sensorsList.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(sensorsList.get(i).getName());
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }

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

        updateSettingsLabels();
        updateRangesLabels();
        updateValuesLabels();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        setContentView(R.layout.activity_settings);
        initWidgets();
    }

    public void initWidgets() {
        findViewById(R.id.fab).setOnClickListener(this);

        forwardSetting = findViewById(R.id.tw_fs);
        forwardSetting.setOnClickListener(this);
        rightSetting = findViewById(R.id.tw_rs);
        rightSetting.setOnClickListener(this);
        backwardSetting = findViewById(R.id.tw_bs);
        backwardSetting.setOnClickListener(this);
        leftSetting = findViewById(R.id.tw_ls);
        leftSetting.setOnClickListener(this);

        forwardRange = findViewById(R.id.tw_fr);
        forwardRange.setOnClickListener(this);
        rightRange = findViewById(R.id.tw_rr);
        rightRange.setOnClickListener(this);
        backwardRange = findViewById(R.id.tw_br);
        backwardRange.setOnClickListener(this);
        leftRange = findViewById(R.id.tw_lr);
        leftRange.setOnClickListener(this);

        forwardValue = findViewById(R.id.tw_fp);
        forwardValue.setOnClickListener(this);
        rightValue = findViewById(R.id.tw_rp);
        rightValue.setOnClickListener(this);
        backwardValue = findViewById(R.id.tw_bp);
        backwardValue.setOnClickListener(this);
        leftValue = findViewById(R.id.tw_lp);
        leftValue.setOnClickListener(this);

        findViewById(R.id.btn_standard).setOnClickListener(this);

        // Setup dialogs layouts
        sensorsDialog.setContentView(R.layout.radiobuttons_dialog);
        rangeDialog.setContentView(R.layout.range_dialog);
        valueDialog.setContentView(R.layout.value_dialog);

        radioGroup = sensorsDialog.findViewById(R.id.radio_group);
        sensorsDialog.findViewById(R.id.btn_close).setOnClickListener(this);

        rangeDialog.findViewById(R.id.btn_close2).setOnClickListener(this);
        minValue = rangeDialog.findViewById(R.id.et_min);
        maxValue = rangeDialog.findViewById(R.id.et_max);

        valueDialog.findViewById(R.id.btn_close3).setOnClickListener(this);
        valueChosen = valueDialog.findViewById(R.id.et_val);
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

            updateSettingsLabels();
            updateRangesLabels();
            updateValuesLabels();

            savePreferences();
            return true;
        } else {
            Toast.makeText(this, getString(R.string.settings_problem_standard), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void updateSettingsLabels() {
        forwardSetting.setText(sensorsList.get(settings[0]).getName().length() > 16 ? sensorsList.get(settings[0]).getName().substring(0, 16) + "..." : sensorsList.get(settings[0]).getName());
        rightSetting.setText(sensorsList.get(settings[1]).getName().length() > 16 ? sensorsList.get(settings[1]).getName().substring(0, 16) + "..." : sensorsList.get(settings[1]).getName());
        backwardSetting.setText(sensorsList.get(settings[2]).getName().length() > 16 ? sensorsList.get(settings[2]).getName().substring(0, 16) + "..." : sensorsList.get(settings[2]).getName());
        leftSetting.setText(sensorsList.get(settings[3]).getName().length() > 16 ? sensorsList.get(settings[3]).getName().substring(0, 16) + "..." : sensorsList.get(settings[3]).getName());
    }

    private void updateRangesLabels() {
        forwardRange.setText(ranges[0].length() > 16 ? ranges[0].substring(0, 16) + "..." : ranges[0]);
        rightRange.setText(ranges[1].length() > 16 ? ranges[1].substring(0, 16) + "..." : ranges[1]);
        backwardRange.setText(ranges[2].length() > 16 ? ranges[2].substring(0, 16) + "..." : ranges[2]);
        leftRange.setText(ranges[3].length() > 16 ? ranges[3].substring(0, 16) + "..." : ranges[3]);
    }

    private void updateValuesLabels() {
        forwardValue.setText(String.valueOf(values[0]));
        rightValue.setText(String.valueOf(values[1]));
        backwardValue.setText(String.valueOf(values[2]));
        leftValue.setText(String.valueOf(values[3]));
    }

    private void showSensorsDialog() {
        radioGroup.check(radioGroup.getChildAt(settings[selectedSetting]).getId());
        sensorsDialog.show();
    }

    private void showRangeDialog() {
        String[] values = ranges[selectedSetting].split(":");
        minValue.setText(values[0]);
        maxValue.setText(values[1]);

        rangeDialog.show();
    }

    private void showValueDialog() {
        valueChosen.setText(String.valueOf(values[selectedSetting]));
        valueDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                savePreferences();
                Snackbar.make(v, getString(R.string.settings_saved), Snackbar.LENGTH_LONG).show();
                break;
            case R.id.btn_close: // Sensors dialog
                settings[selectedSetting] = radioGroup.getCheckedRadioButtonId();
                updateSettingsLabels();
                sensorsDialog.dismiss();
                break;
            case R.id.btn_close2: // Ranges dialog
                if(Double.parseDouble(minValue.getText().toString()) > 1 || Double.parseDouble(maxValue.getText().toString()) > 1) {
                    Toast.makeText(this, getString(R.string.settings_err1), Toast.LENGTH_SHORT).show();
                } else if(Double.parseDouble(minValue.getText().toString()) > Double.parseDouble(maxValue.getText().toString())) {
                    Toast.makeText(this, getString(R.string.settings_err2), Toast.LENGTH_SHORT).show();
                } else {
                    ranges[selectedSetting] = minValue.getText().toString() + ":" + maxValue.getText().toString();
                }
                updateRangesLabels();
                rangeDialog.dismiss();
                break;
            case R.id.btn_close3: // Values dialog
                if(Integer.parseInt(valueChosen.getText().toString()) > 5 || Integer.parseInt(valueChosen.getText().toString()) < 0)
                    Toast.makeText(this, getString(R.string.settings_err3), Toast.LENGTH_SHORT).show();
                else
                    values[selectedSetting] = Integer.parseInt(valueChosen.getText().toString());
                updateValuesLabels();
                valueDialog.dismiss();
                break;
            case R.id.btn_standard:
                setStandardSettings();
                break;
            case R.id.tw_fs:
                selectedSetting = 0;
                showSensorsDialog();
                break;
            case R.id.tw_rs:
                selectedSetting = 1;
                showSensorsDialog();
                break;
            case R.id.tw_bs:
                selectedSetting = 2;
                showSensorsDialog();
                break;
            case R.id.tw_ls:
                selectedSetting = 3;
                showSensorsDialog();
                break;
            case R.id.tw_fr:
                selectedSetting = 0;
                showRangeDialog();
                break;
            case R.id.tw_rr:
                selectedSetting = 1;
                showRangeDialog();
                break;
            case R.id.tw_br:
                selectedSetting = 2;
                showRangeDialog();
                break;
            case R.id.tw_lr:
                selectedSetting = 3;
                showRangeDialog();
                break;
            case R.id.tw_fp:
                selectedSetting = 0;
                showValueDialog();
                break;
            case R.id.tw_rp:
                selectedSetting = 1;
                showValueDialog();
                break;
            case R.id.tw_bp:
                selectedSetting = 2;
                showValueDialog();
                break;
            case R.id.tw_lp:
                selectedSetting = 3;
                showValueDialog();
                break;
        }
    }

    private void savePreferences() {
        for(int i = 0; i < 4; i++) {
            if(preferences.getInt("setting" + i, -1) != settings[i]) { // Save sensors selected (if changed)
                preferences.edit().putInt("setting" + i, settings[i]).apply();
                preferences.edit().putBoolean("changedSensor" + i, true).apply(); // The sensor has been changed
            }
            if(!preferences.getString("range" + i, "").equals(ranges[i])) // Save range (if changed)
                preferences.edit().putString("range" + i, ranges[i]).apply();
            if(preferences.getInt("value" + i, -1) != values[i]) // Save value (if changed)
                preferences.edit().putInt("value" + i, values[i]).apply();
        }
    }
}
