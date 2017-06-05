package ssd.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;

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

    public void insert(LogData log){
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }

    public void addDevice(String name,String addr,String port){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO DEVICE (DEVICE_NAME,DEVICE_ADDR,DEVICE_PORT) " +
        "VALUES ('" + name + "','" + addr + "','" + port + "');");
        db.close();
    }

    public ArrayList<String> getDeviceList(){
        ArrayList<String> list = new ArrayList<>();
        list.add("기기");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT DEVICE_NAME FROM DEVICE",null);
        for(;cur.moveToNext();){
            list.add(cur.getString(0));
        }
        return list;
    }

    public ArrayList<String> getUserList(){
        ArrayList<String> list = new ArrayList<>();
        list.add("유저");
        return list;
    }

    public String read(){
        StringBuffer result = new StringBuffer();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM LOG", null);
        while (cur.moveToNext()) {
            result.append("유저 id : ");
            result.append(cur.getInt(0));
            result.append("디바이스 id : ");
            result.append(cur.getInt(1));
            result.append("날짜 : ");
            result.append(cur.getInt(2));
            result.append("\n");
        }
        db.close();
        return result.toString();
    }
}