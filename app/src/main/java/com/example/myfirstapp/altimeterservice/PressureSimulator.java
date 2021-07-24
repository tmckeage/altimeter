package com.example.myfirstapp.altimeterservice;

import android.hardware.SensorEvent;

public class PressureSimulator implements Runnable{
    private PressureEventListener pressureEventListener;

    public PressureSimulator(PressureEventListener pel){
        pressureEventListener = pel;
    }

    @Override
    public void run() {
        //pressureEventListener.onSensorChanged();
    }
}
