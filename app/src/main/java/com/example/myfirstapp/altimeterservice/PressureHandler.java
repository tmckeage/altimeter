package com.example.myfirstapp.altimeterservice;

import android.os.Handler;
import android.os.Message;

public class PressureHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        //It is currently not expected that the Pressure thread will need to recieve
        //messages but the hook is here regardless
    }
}
