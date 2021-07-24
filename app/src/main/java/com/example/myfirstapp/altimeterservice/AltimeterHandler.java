package com.example.myfirstapp.altimeterservice;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class AltimeterHandler extends Handler {
    private PressureEvent lastEvent;
    private SensorManager sensorManager;
    private int noFallCnt;
    private int calibrationPressure;
    private AltimeterService altimeterService;
    private Jump jump;
    private AltitudeTalker altitudeTalker;

    public AltimeterHandler(SensorManager sm){
        this(sm, "feet", 1000);
    }


    public AltimeterHandler(SensorManager sm, String units){
        this(sm, units, 1000);
    }

    public AltimeterHandler(SensorManager sm, String units, int increment){
        noFallCnt = 0;
        lastEvent = null;
        sensorManager = sm;
        calibrationPressure = 0;
        altitudeTalker = new AltitudeTalker(increment, units);

    }

    @Override
    public void handleMessage(Message msg) {
        Bundle b = msg.getData();
        String json = b.getString("Pressure Event");
        PressureEvent pe;
        if (json != null){
            pe = PressureEvent.fromJson(json);
        } else {
            //no pressure event found
            return;
        }
        float altDiff;
        if (lastEvent != null) {
            altDiff = sensorManager.getAltitude(lastEvent.getPressure(), pe.getPressure());
            //TODO: alt difference might not be the best way to do this, or at least set calibration altitude
            // to a useful default, possibly from a weather API
            if (altDiff < 2 && altDiff > -2) {
                if (noFallCnt > 600) {
                    //calibration time
                    calibrationPressure = pe.getPressure();
                    noFallCnt = 0;
                    if (jump != null) {
                        jump.finish();
                        //TODO: Send to log book and persist
                        jump = null;
                    }
                } else {
                    noFallCnt++;
                }
            } else if(calibrationPressure != 0) { //fallRate is Significant and callibration is available
                if (jump == null){ jump = new Jump(calibrationPressure);}
                noFallCnt = 0;
                jump.addPressureEvent(pe);
                String statement = altitudeTalker.shouldAnythingBeSaid(pe, calibrationPressure);
                if (statement != null){
                    Message outMsg = altimeterService.getAltimeterServiceHandler().obtainMessage();
                    Bundle bun = new Bundle();
                    bun.putString("Speak", statement);
                    outMsg.setData(bun);
                    altimeterService.getAltimeterServiceHandler().sendMessage(outMsg);
                }
            } else { //no calibration pressure
                //handle no calibration pressure
            }
        }
        lastEvent = pe;



    }
}
