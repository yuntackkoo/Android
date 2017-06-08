package ssd.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import log.LogData;

/**
 * Created by Koo on 2017-06-05.
 */

public class SsdDB extends SQLiteOpenHelper {
    static public final String DBNAME = "LOG.db";

    public SsdDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE LOG (LOG_USER_ID INTEGER,DEVICE_ID INTEGER,DATE INTEGER);");
        db.execSQL("CREATE TABLE DEVICE (DEVICE_ID INTEGER PRIMARY KEY AUTOINCREMENT,DEVICE_NAME TEXT,DEVICE_ADDR TEXT," +
                "DEVICE_PORT INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertLog(LogData log,byte id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO LOG VALUES(" + id + "," + log.getId() + "," + log.getDate() + ");");
        db.close();
    }

    public byte serchDevice(String name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DEVICE_ID FROM DEVICE WHERE DEVICE_NAME = '" + name + "';",null);
        cursor.moveToFirst();
        db.close();
        return (byte)cursor.getInt(0);
    }

    //디바이스를 추가 이름,주소,포트
    public void addDevice(String name,String addr,String port){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO DEVICE (DEVICE_NAME,DEVICE_ADDR,DEVICE_PORT) " +
        "VALUES ('" + name + "','" + addr + "','" + port + "');");
        db.close();
    }

    //디바이스 목록을 조회
    public ArrayList<String> getDeviceList(){
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT DEVICE_NAME FROM DEVICE",null);
        for(;cur.moveToNext();){
            list.add(cur.getString(0));
        }
        return list;
    }

    //유저 목록을 조회
    public ArrayList<String> getUserList(){
        ArrayList<String> list = new ArrayList<>();
        return list;
    }

    //로그 삭제 지금은 테스트용으로 디바이스 목록 삭제
    public void logDel(){
        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL("DROP TABLE DEVICE;");
//        db.execSQL("CREATE TABLE DEVICE (DEVICE_ID INTEGER PRIMARY KEY AUTOINCREMENT,DEVICE_NAME TEXT,DEVICE_ADDR TEXT," +
//                "DEVICE_PORT INTEGER);");
        db.execSQL("DROP TABLE LOG");
        db.execSQL("CREATE TABLE LOG (LOG_USER_ID INTEGER,DEVICE_ID INTEGER,DATE INTEGER);");
        db.close();
    }

    //기기에 대한 포트와 주소를 조회
    public Map<String,String> deviceSerch(String name){
        Map<String,String> tmp = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DEVICE_ID,DEVICE_ADDR,DEVICE_PORT FROM DEVICE WHERE DEVICE_NAME = '" + name + "';",null);
        cursor.moveToFirst();
        Byte b = new Byte((byte)cursor.getInt(0));
        tmp.put("id",b.toString());
        tmp.put("addr",cursor.getString(1));
        tmp.put("port",cursor.getString(2));
        db.close();
        return tmp;
    }

    public Map<String,String> read(){
        StringBuffer userid = new StringBuffer();
        StringBuffer deviceid = new StringBuffer();
        StringBuffer date = new StringBuffer();
        SQLiteDatabase db = getReadableDatabase();
        Map<String,String> map = new HashMap<>();

        Cursor cur = db.rawQuery("SELECT * FROM LOG", null);
        while (cur.moveToNext()) {
            userid.append(cur.getInt(0));
            userid.append("\n");
            deviceid.append(cur.getInt(1));
            deviceid.append("\n");
            date.append(cur.getInt(2));
            date.append("\n");
        }
        map.put("userid",userid.toString());
        map.put("deviceid",deviceid.toString());
        map.put("date",date.toString());
        db.close();
        return map;
    }
}