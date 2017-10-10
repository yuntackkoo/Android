package ssd.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
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
        db.execSQL("CREATE TABLE LOG (DEVICE_ID INTEGER,DATE TEXT);");
        db.execSQL("CREATE TABLE DEVICE (DEVICE_ID INTEGER PRIMARY KEY AUTOINCREMENT,DEVICE_NAME TEXT,DEVICE_ADDR TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertLog(LogData log,byte id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO LOG VALUES (" + id +",'" + log.dateString() + "');");
        db.close();
    }

    public byte serchDevice(String name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DEVICE_ID FROM DEVICE WHERE DEVICE_NAME = '" + name + "';",null);
        cursor.moveToFirst();
        db.close();
        return (byte)cursor.getInt(0);
    }

    //디바이스를 추가 이름,주소
    public void addDevice(String name,String addr){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO DEVICE (DEVICE_NAME,DEVICE_ADDR) " +
        "VALUES ('" + name + "','" + addr + "');");
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

    //로그 삭제 지금은 테스트용으로 디바이스 목록 삭제
    public void logDel(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE DEVICE;");
        db.execSQL("CREATE TABLE DEVICE (DEVICE_ID INTEGER PRIMARY KEY AUTOINCREMENT,DEVICE_NAME TEXT,DEVICE_ADDR TEXT);");
        db.execSQL("DROP TABLE LOG");
        db.execSQL("CREATE TABLE LOG (DEVICE_ID INTEGER,DATE TEXT);");
        db.close();
    }

    //기기에 대한 포트와 주소를 조회
    public Map<String,String> deviceSerch(String name){
        Map<String,String> tmp = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DEVICE_ID,DEVICE_ADDR FROM DEVICE WHERE DEVICE_NAME = '" + name + "';",null);
        cursor.moveToFirst();
        Byte b = new Byte((byte)cursor.getInt(0));
        tmp.put("id",b.toString());
        tmp.put("addr",cursor.getString(1));
        db.close();
        return tmp;
    }

    public Map<String,String> read(){
        StringBuffer deviceid = new StringBuffer();
        StringBuffer date = new StringBuffer();
        SQLiteDatabase db = getReadableDatabase();
        Map<String,String> map = new HashMap<>();

        Cursor cur = db.rawQuery("SELECT DEVICE.DEVICE_NAME, LOG.DATE " +
                "FROM LOG INNER JOIN DEVICE ON LOG.DEVICE_ID = DEVICE.DEVICE_ID ", null);
        while (cur.moveToNext()) {
            deviceid.append(cur.getString(0));
            deviceid.append("\n");
            date.append(cur.getString(1));
            date.append("\n");
        }
        map.put("deviceid",deviceid.toString());
        map.put("date",date.toString());
        db.close();
        return map;
    }

    public Map<String,String> read(String name){
        StringBuffer deviceid = new StringBuffer();
        StringBuffer date = new StringBuffer();
        SQLiteDatabase db = getReadableDatabase();
        Map<String,String> map = new HashMap<>();

        Cursor cur = db.rawQuery("SELECT DEVICE.DEVICE_NAME, LOG.DATE " +
                "FROM LOG INNER JOIN DEVICE ON LOG.DEVICE_ID = DEVICE.DEVICE_ID" +
                " WHERE DEVICE.DEVICE_NAME = '" + name + "';", null);
        while (cur.moveToNext()) {
            deviceid.append(cur.getString(0));
            deviceid.append("\n");

            date.append(cur.getString(1));
            date.append("\n");
        }
        map.put("deviceid",deviceid.toString());
        map.put("date",date.toString());
        db.close();
        return map;
    }

    public String getDeviceName(byte id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT DEVICE_NAME FROM DEVICE " +
                "WHERE DEVICE_ID = " + id + ";",null);
        cur.moveToFirst();
        String tmp = cur.getString(0);
        return tmp;
    }

}