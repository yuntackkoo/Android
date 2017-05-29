package com.example.koo.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class BackGround extends Service {
    Notification noti;
    NotificationManager notimgr;
    ConfigDataManager config = null;

    public BackGround() {
        config = ConfigDataManager.getInstance(this.getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        if(config.getData().isAuto_update()) {
            Toast.makeText(BackGround.this, "asdf", Toast.LENGTH_LONG).show();
            if (notimgr == null) {
                notimgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (noti == null) {
                noti = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Content Title")
                        .setContentText("Content Text")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker("알림")
                        .build();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}