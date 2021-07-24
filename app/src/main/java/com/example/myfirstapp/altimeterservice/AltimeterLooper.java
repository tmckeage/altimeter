package com.example.myfirstapp.altimeterservice;

import android.annotation.SuppressLint;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

public class AltimeterLooper extends Thread  {
    private AltimeterHandler altimeterHandler;
    private AltimeterService altimeterService;
    private long startMilli = 0;
    private SensorManager sensorManager;

    public AltimeterHandler getAltimeterHandler(){
        return altimeterHandler;
    }

    public AltimeterLooper(SensorManager sm, AltimeterService as){
        altimeterService = as;
        sensorManager = sm;
    }


    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        Looper.prepare();

        altimeterHandler = new AltimeterHandler(sensorManager);
        Bundle bundle = new Bundle();
        bundle.putString("Altimeter Service Message", "AltimeterHandler Set");
        Message msg = new Message();
        msg.setData(bundle);
        altimeterService.getAltimeterServiceHandler().sendMessage(msg);

        Looper.loop();

    }
}

