package com.example.myfirstapp.altimeterservice;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

public class PressureLooper extends Thread {
    private SensorManager sm;
    private AltimeterHandler ah;
    private Sensor pressureSensor;
    private PressureHandler pressureHandler;

    public PressureHandler getPressureHandler(){
        return pressureHandler;
    }

    public PressureLooper(SensorManager sm, AltimeterHandler ah){
        this.sm = sm;
        this.ah = ah;
        pressureSensor = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    @Override
    public void run(){
        PressureEventListener pel = new PressureEventListener(sm, ah);
        sm.registerListener(pel, pressureSensor, sm.SENSOR_DELAY_NORMAL);

        Looper.prepare();
        pressureHandler = new PressureHandler();
        Looper.loop();
    }
}
