package com.example.myfirstapp.altimeterservice;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import com.google.gson.Gson;

public class PressureEvent {
    private int pressure;
    private long timestamp;

    public int getPressure(){
        return pressure;
    }

    public long getTimeStamp(){
        return timestamp;
    }

    public long getMillis(){
        return getTimeStamp()/1000000;
    }

    public int getAltitude(int groundPressure, String unit){
        int altM =  (int)SensorManager.getAltitude(groundPressure, getPressure());
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
        this.pressure = Math.round(se.values[0]);
        this.timestamp = se.timestamp;
    }

    public PressureEvent(MockSensorEvent se, SensorManager sm){
        this.pressure = Math.round(se.values[0]);
        this.timestamp = se.timestamp;
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
