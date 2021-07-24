package com.example.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.myfirstapp.altimeterservice.AltimeterService;

import java.util.Locale;


public class MainActivity extends Activity implements SensorEventListener, TextToSpeech.OnInitListener {
    private static final int TTS_CHECK_CODE = 101;

    Button btnStartService, btnStopService;
    private SensorManager sensorManager;
    private Sensor pressure;
    private static final int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech mTts;
    private int lastSpokenAltitude = 0;
    private float millibarsOfPressure = 1000f;
    private int calibrationPressure = 1000;
    private int increments = 500;
    private boolean is_paused = false;
    private float pressureIncrement = 0.2f;


    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);



        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TTS_CHECK_CODE);

        Spinner breakAltitude = (Spinner) findViewById(R.id.break_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.break_altitudes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breakAltitude.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TTS_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance

            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }

        Intent serviceIntent = new Intent(this, AltimeterService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        serviceIntent.putExtra("requestCode", requestCode);
        serviceIntent.putExtra("resultCode", resultCode);
        ContextCompat.startForegroundService(this, serviceIntent);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!is_paused) {
            millibarsOfPressure = millibarsOfPressure - pressureIncrement;
        }

        TextView rndAltView = findViewById(R.id.rnd_alt);
        TextView realAltView = findViewById(R.id.real_alt);
        TextView incrementView = findViewById(R.id.increment);
        TextView lstSpknView = findViewById(R.id.lst_spk_alt);

        int altitude = (int)(sensorManager.getAltitude(1100f, millibarsOfPressure) * 3.28084);
        int roundedAlt = altitude - (altitude%increments);
        incrementView.setText((Integer.toString(increments)));
        realAltView.setText(Integer.toString(altitude));
        rndAltView.setText(Integer.toString(roundedAlt));
        lstSpknView.setText(Integer.toString(lastSpokenAltitude));
        if(roundedAlt >= lastSpokenAltitude + increments ||
                roundedAlt <= lastSpokenAltitude - increments){
            mTts.speak(
                    roundedAlt + " feet",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null);
            lastSpokenAltitude = roundedAlt;
            if(roundedAlt >= 14000){
                pressureIncrement = -1f;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPausePush(View view){
        is_paused = !is_paused;
        Button pauser = findViewById(R.id.pause);
        pauser.setText(is_paused ? "Resume" : "Pause");
    }

    public void startService(View view) {
        Intent serviceIntent = new Intent(this, AltimeterService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        startForegroundService(serviceIntent);
    }

    public void stopService(View view) {
        Intent serviceIntent = new Intent(this, AltimeterService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onInit(int status) {
        mTts.setLanguage(Locale.US);
    }
}