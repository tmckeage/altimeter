package com.example.myfirstapp.altimeterservice;

public class AltitudeTalker {
    private int lastSpokenAltitude = 0;
    private PressureEvent lastPressureEvent;
    private int increments;
    private boolean isFalling = false;
    private String units;

    public AltitudeTalker(){
        this(1000, "feet");
    }
    public AltitudeTalker(String units){
        this(1000, units);
    }

    public AltitudeTalker(int inc, String units){
        increments = inc;
        this.units = units;
    }

    public String shouldAnythingBeSaid(PressureEvent pe, int cPressure) {
        int altitude = pe.getAltitude(cPressure, units);
        int roundedAlt = altitude - (altitude%increments);
        if(roundedAlt >= lastSpokenAltitude + increments ||
                roundedAlt <= lastSpokenAltitude - increments){
            lastSpokenAltitude = roundedAlt;
            return roundedAlt + " " + units;
        }
        return null;
    }
}
