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
import protocol.OperationCode;
import protocol.Packet;
import protocol.PacketProcess;
import protocol.OperationCode;

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
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(reciver);
        super.onDestroy();
    }

    class Reciver extends BroadcastReceiver {
        Comunication currentcom = null;
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("CONNECT")) {
                final String name = intent.getExtras().getString("name");
                Map<String, String> tmp = db.deviceSerch(name);
                if(!deviceList.containsKey(name)) {
                    currentcom = new Comunication(tmp.get("addr").toString(), "255", Byte.parseByte(tmp.get("id").toString()));
                    currentcom.setP(new PacketProcess() {
                        @Override
                        public void doProcess(Comunication currentcom) {
                            byte[] tmp = new byte[5];
                            //boolean invail = false;
                            boolean invail = true;
                            currentcom.setRecive(currentcom.getComPacket().getCurrent());
                            if (currentcom.getSeq_num() != 0) {
                                //invail = recive.comp(seq_num);
                            }
                            if (invail && currentcom.getRecive() != null) {
                                switch (currentcom.getRecive().getCode()) {
                                    //응답
                                    case OperationCode.Reponse:
                                        currentcom.setSeq_num(currentcom.getRecive().getNonce());
                                        System.out.println(currentcom.getSeq_num() + " 시퀸스 넘버");
                                        break;
                                    //초기 등록 또는 추가 등록시 키교환 확인
                                    case OperationCode.Confirm_KeyEx:
                                        break;
                                    //키 교환 요구 받을시 키 제공
                                    case OperationCode.KeyOffer:
                                        currentcom.getComPacket().getCryptoModule().setKey(currentcom.getRecive().getData());
                                        break;
                                    //로그 요청 받을시 로그 응답
                                    case OperationCode.Offer_Data:
                                        break;
                                    //다른 기기가 문을 열때
                                    case OperationCode.Unlock_Other:
                                        if (deviceList.get(name).getComPacket().getCurrent().getCode() == 64) {
                                            Notification noti = new Notification.Builder(getApplicationContext())
                                                    .setSmallIcon(R.drawable.bt_unlock)
                                                    .setContentTitle(new Byte(deviceList.get(name).getThisId()).toString())
                                                    .setContentText("잠금해제")
                                                    .setTicker("알림")
                                                    .build();
                                            notimgr.notify(deviceList.get(name).getThisId(), noti);
                                            Packet p = deviceList.get(name).getRecive();
                                            System.arraycopy(p.getData(), 0, tmp, 0, 5);
                                            LogData log = new LogData(tmp);
                                            Log.e(new Integer(log.getDate()).toString(), "asdf");
                                            db.insertLog(log, (byte) 0);
                                        }
                                        break;
                                }
                            }
                            currentcom.setSeq_num(currentcom.getSeq_num()+1);
                            currentcom.setRecive(null);
                        }
                    });
                    deviceList.put(name, currentcom);
                    currentcom.start();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Packet p = new Packet();
                            p.setCode(OperationCode.Key_Ex);
                            p.setId(0);
                            p.setData("asdf".getBytes());
                            currentcom.send(p);
                            for (int i = 0; i < p.getData().length; i++) {
                                System.out.print((char) p.getData()[i]);
                                System.out.println();
                            }
                        }
                    }, 1000);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Packet p = new Packet();
                            p.setCode(OperationCode.UnLock);
                            p.setId(0);
                            p.setData(new LogData((byte) 0).getByte());
                            currentcom.send(p);
                        }
                    }, 2000);
                } else{
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Packet p = new Packet();
                            p.setCode(OperationCode.UnLock);
                            p.setId(0);
                            p.setData(new LogData((byte) 0).getByte());
                            currentcom.send(p);
                        }
                    }, 1000);
                }
            }
        }
    }
}