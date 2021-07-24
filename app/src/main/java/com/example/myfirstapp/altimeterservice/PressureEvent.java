package com.example.myfirstapp.altimeterservice;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import com.google.gson.Gson;

public class PressureEvent {
    private SensorEvent pressureEvent;
    private SensorManager pressureManager;

    public int getPressure(){
        return Math.round(pressureEvent.values[0]);
    }

    public long getTimeStamp(){
        return pressureEvent.timestamp;
    }

    public long getMillis(){
        return getTimeStamp()/1000000;
    }

    public int getAltitude(int groundPressure, String unit){
        int altM =  (int)pressureManager.getAltitude(groundPressure, getPressure());
        if (unit != null && unit.equals("meters")){
            return altM;
        }
        return (int)(altM * 3.28084);

    }

    public int getDistanceTraveled(PressureEvent former, int calibrationPressure, String unit){
        return getAltitude(calibrationPressure, unit) -
                former.getAltitude(calibrationPressure, unit);
    }


    public int getMilliDifference(PressureEvent former){
        long diff = getMillis() - former.getMillis();
        return (int)diff;
    }

    public int getAvgAltitude(PressureEvent former, int calibrationPressure, String unit){
        return (getAltitude(calibrationPressure, unit) +
                former.getAltitude(calibrationPressure, unit))/2;
    }

    public int getDescentPerSecond(PressureEvent former, int calibrationPressure, String unit){
        return (getDistanceTraveled(former, calibrationPressure, unit) * 1000)
                / getMilliDifference(former);
    }

    public PressureEvent(SensorEvent se, SensorManager sm){
        pressureEvent = se;
        pressureManager = sm;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static PressureEvent fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, PressureEvent.class);
    }

}
