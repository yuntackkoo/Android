package com.example.koo.myapplication;

import android.content.Context;

import protocol.ComPacket;
import protocol.PacketProcess;
import protocol.TcpComPacket;

/**
 * Created by Koo on 2017-04-23.
 */

public class ServiceThread extends Thread {
    private TcpComPacket tcpsocket = null;
    private boolean state = true;
    private PacketProcess p;
    private Context context = null;

    public ServiceThread(Context c, PacketProcess process) {
        tcpsocket = new TcpComPacket("255");
        context = c;
        tcpsocket.setProcess(process);
    }

    @Override
    public void run() {
        for(;;) {
            tcpsocket.receive();
        }
    }
}
