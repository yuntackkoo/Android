package com.example.koo.myapplication;

import android.content.Context;

import protocol.PacketProcess;
import protocol.UdpComPacket;

/**
 * Created by Koo on 2017-04-23.
 */

public class ServiceThread extends Thread {
    private UdpComPacket udpsocket = null;
    private boolean state = true;
    private PacketProcess p;

    public ServiceThread(Context c,PacketProcess process) {
        p = process;

    }

    @Override
    public void run() {
        for(;;) {
            p.doProcess();
            try {
                this.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
