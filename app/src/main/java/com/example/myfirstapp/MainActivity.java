package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;


public class MainActivity extends Activity implements SensorEventListener, TextToSpeech.OnInitListener {
    private SensorManager sensorManager;
    private Sensor pressure;
    private static final int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech mTts;
    private int lastSpokenAltitude = 0;
    private int millibarsOfPressure = 1000;
    private int calibrationPressure = 1000;


    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==MY_DATA_CHECK_CODE) {
            if(resultCode==TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                // if TTS resources are available you instanciate your TextToSpeech object
                mTts = new TextToSpeech(this, this);
            } else {
                // else you ask the system to install it
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        millibarsOfPressure = millibarsOfPressure - 1;
        TextView pressureView = findViewById(R.id.pressure);
        TextView altitudeView = findViewById(R.id.altitude);
        int altitude = (int)(sensorManager.getAltitude(1100f, millibarsOfPressure) * 3.28084);
        altitudeView.setText(Float.toString(altitude));
        pressureView.setText(Float.toString(millibarsOfPressure));
        if(altitude > lastSpokenAltitude+1000 || altitude < lastSpokenAltitude - 1000){
            mTts.speak(Float.toString(altitude) + "feet", TextToSpeech.QUEUE_FLUSH, null);
            lastSpokenAltitude = altitude;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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