package com.example.koo.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import config.ConfigData;
import protocol.PacketProcess;

public class BackGround extends Service {
    ServiceThread service = null;
    Notification noti;
    NotificationManager notimgr;

    public BackGround() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(BackGround.this,"asdf",Toast.LENGTH_LONG).show();
        if(notimgr == null) {
            notimgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(noti == null) {
            noti = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Content Title")
                    .setContentText("Content Text")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("알림")
                    .build();
        }
        if(service == null) {
            service = new ServiceThread(this, new PacketProcess() {
                @Override
                public void doProcess() {
                    notimgr.notify(777, noti);
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!service.isAlive()) {
            service.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        service = null;
        super.onDestroy();
    }
}