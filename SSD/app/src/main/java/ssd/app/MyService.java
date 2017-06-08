package ssd.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import protocol.Comunication;

public class MyService extends Service {
    private SsdDB db = null;
    private Map<String,Comunication> deviceList = null;

    public MyService() {
    }

    @Override
    public void onCreate() {
        deviceList = new HashMap<>();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new SsdDB(this.getApplicationContext(),SsdDB.DBNAME,null,1);
        IntentFilter filter = new IntentFilter("CONNECT");
        registerReceiver(new reciver(),filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class reciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("CONNECT")) {
                String name = intent.getExtras().getString("name");
                Map<String, String> tmp = db.deviceSerch(name);
                deviceList.put(name,new Comunication(tmp.get("addr").toString(), tmp.get("port").toString(), Byte.parseByte(tmp.get("id").toString())));
                deviceList.get(name).start();
            }
        }
    }
}