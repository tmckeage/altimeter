package com.example.myfirstapp.altimeterservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;


import androidx.annotation.NonNull;

import com.example.myfirstapp.MainActivity;
import com.example.myfirstapp.R;

import java.util.ArrayList;
import java.util.Locale;

public class AltimeterService extends Service implements TextToSpeech.OnInitListener, Handler.Callback {
    public static final String CHANNEL_ID = "AltimeterServiceChannel";
    private PressureLooper pressureLooper;
    private AltimeterLooper altimeterLooper;
    private Handler altimeterServiceHandler;
    private SensorManager sm;
    private TextToSpeech textToSpeech;
    private boolean isTtsInit;

    public Handler getAltimeterServiceHandler() {
        return altimeterServiceHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        super.onCreate();
        altimeterServiceHandler = new Handler(this);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        altimeterLooper = new AltimeterLooper(sm, this);
        altimeterLooper.run();

        textToSpeech = new TextToSpeech(getApplicationContext(), this);


    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pn = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Altimeter Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification);

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsInit = true;
            }
        }
    }


    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Bundle bun = msg.getData();
        ArrayList<String> messageData = bun.getStringArrayList("Altimeter Service Message");
        if (messageData.get(0).equals("Speak")) {
            speak(messageData.get(1));
        } else if (messageData.get(0).equals("AltimeterHandler Set")) {
            pressureLooper = new PressureLooper(sm, altimeterLooper.getAltimeterHandler());
            pressureLooper.run();
        }
        return true;
    }

    private void speak(String message){
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
