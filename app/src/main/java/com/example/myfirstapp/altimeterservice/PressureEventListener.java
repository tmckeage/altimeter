package com.example.myfirstapp.altimeterservice;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;


public class PressureEventListener implements SensorEventListener {
    private SensorManager sm;
    private long lastPublishTime;
    private AltimeterHandler altimeterHandler;
    private boolean isSimulating = false;
    private PressureSimulator simulator;

    public void startSimulation(){
        sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_PRESSURE));
        isSimulating = true;
    }

    public void stopSimulation(){
        sm.registerListener(
                this,
                sm.getDefaultSensor(Sensor.TYPE_PRESSURE),
                sm.SENSOR_DELAY_NORMAL);
        isSimulating = false;
    }

    public PressureEventListener(SensorManager sm, AltimeterHandler ah){
        this.sm = sm;
        lastPublishTime = 0;
        altimeterHandler = ah;
    }

    private void messageSender(PressureEvent pe){
        Message msg = altimeterHandler.obtainMessage();
        Bundle bun = new Bundle();
        bun.putString("Pressure Event", pe.toJson());
        msg.setData(bun);
        altimeterHandler.sendMessage(msg);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        long eventStamp = event.timestamp/100000000;
        if (eventStamp > lastPublishTime){
            lastPublishTime = eventStamp;
            PressureEvent pe = new PressureEvent(event, sm);
            messageSender(pe);


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
