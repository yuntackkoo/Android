package com.example.koo.myapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import protocol.PacketProcess;
import protocol.UdpComPacket;

/**
 * Created by Koo on 2017-04-23.
 */

public class ServiceThread extends Thread {
    private UdpComPacket udpsocket = null;
    private boolean state = true;
    private PacketProcess p;
    private Context context = null;

    public ServiceThread(Context c, PacketProcess process) {
        udpsocket = new UdpComPacket("15000");
        context = c;
        udpsocket.setProcess(process);
    }

    @Override
    public void run() {
        for(;;) {
            udpsocket.receive();
        }
    }
}
