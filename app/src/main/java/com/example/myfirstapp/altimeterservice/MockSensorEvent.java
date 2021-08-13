package com.example.myfirstapp.altimeterservice;

public class MockSensorEvent  {


    public final float[] values;
    public long timestamp;

    public MockSensorEvent(int value, long timestamp){

        float[] v = {value};
        values = v;
        this.timestamp = timestamp;
    }
}
