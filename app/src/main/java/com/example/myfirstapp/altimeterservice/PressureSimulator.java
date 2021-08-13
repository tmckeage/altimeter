package com.example.myfirstapp.altimeterservice;

import android.hardware.SensorEvent;
import android.os.Looper;

public class PressureSimulator implements Runnable{
    private PressureEventListener pressureEventListener;

    public PressureSimulator(PressureEventListener pel){
        pressureEventListener = pel;
    }

    private void sendEvent(){

    }

    @Override
    public void run() {
        Looper.prepare();

        PressureSimulatorHandler handle = new PressureSimulatorHandler();
    }
}
