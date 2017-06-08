package ssd.app;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import config.ConfigData;
import kr.re.nsr.crypto.util.Pack;
import log.LogData;
import protocol.Comunication;
import protocol.Loging;
import protocol.Packet;

public class Connection extends Service {
    private SsdDB db = null;
    private Map<String,Comunication> deviceList = null;
    private ActivityManager activityManager;
    private Reciver reciver = null;
    private NotificationManager notimgr = null;

    public Connection() {
    }

    @Override
    public void onCreate() {
        deviceList = new HashMap<>();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        notimgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
        filter.addAction("SSD.STOP");
        reciver = new Reciver();
        registerReceiver(reciver,filter);
//        ArrayList<String> list = db.getDeviceList();
//        for(int i=0;i<deviceList.size();i++){
//            String addr = db.deviceSerch(list.get(0)).get("addr");
//            String port = db.deviceSerch(list.get(0)).get("port");
//            Byte id = Byte.parseByte(db.deviceSerch(list.get(0)).get("id"));
//            deviceList.put(id,new Comunication(addr,port,id));
//            deviceList.get(id).setLoging(new Loging() {
//                @Override
//                public void loging() {
//
//                }
//            });
//        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class Reciver extends BroadcastReceiver {
        Comunication currentcom = null;
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("CONNECT")) {
                final String name = intent.getExtras().getString("name");
                Map<String, String> tmp = db.deviceSerch(name);
                currentcom = new Comunication(tmp.get("addr").toString(), tmp.get("port").toString(), Byte.parseByte(tmp.get("id").toString()));
                deviceList.put(name,currentcom);
                currentcom.start();
                currentcom.setLoging(new Loging() {
                    byte[] tmp = new byte[5];
                    @Override
                    public void loging() {
                        if(deviceList.get(name).getComPacket().getCurrent().getCode() == 64) {
                            Notification noti = new Notification.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.bt_unlock)
                                    .setContentTitle(new Byte(deviceList.get(name).getThisId()).toString())
                                    .setContentText("잠금해제")
                                    .setTicker("알림")
                                    .build();
                            notimgr.notify(deviceList.get(name).getThisId(), noti);
                            Packet p = deviceList.get(name).getRecive();
                            System.arraycopy(p.getData(),0,tmp,0,5);
                            LogData log = new LogData(tmp);
                            db.insertLog(log,(byte)0);
                            Log.e("asdf","asdf");
                        }
                    }
                });
            }if(intent.getAction().equals("SSD.STOP")){
                ConfigData config = ConfigDataManager.getInstance(getBaseContext()).getData();
                if(!config.isAuto_update()){
                    unregisterReceiver(reciver);
                    stopSelf();
                }
            }
        }
    }
}