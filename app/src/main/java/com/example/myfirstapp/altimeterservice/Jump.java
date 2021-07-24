package com.example.myfirstapp.altimeterservice;

import java.util.LinkedList;
import java.util.List;

public class Jump {

    private List<PressureEvent> pressureEvents;
    private int calibrationPressure;

    public void addPressureEvent(PressureEvent pe){
        pressureEvents.add(pe);
    }

    public Jump(int cp){
        calibrationPressure = cp;
        pressureEvents = new LinkedList<>();
    }

    public void finish(){

    }


}
