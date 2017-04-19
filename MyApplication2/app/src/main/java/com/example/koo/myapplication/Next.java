package com.example.koo.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.SimpleFormatter;

import log.LogData;

public class Next extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        final SsdDB ssddb = new SsdDB(this.getApplicationContext(),"LOG.db",null,1);
        final TextView result = (TextView) findViewById(R.id.result);

        TextView uid = (TextView) findViewById(R.id.uid);
        final TextView did = (TextView) findViewById(R.id.did);
        final TextView date = (TextView) findViewById(R.id.date);

        Button add = (Button) findViewById(R.id.apend);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte tmp = (byte)Integer.parseInt(did.getText().toString());
                LogData log = new LogData(Integer.parseInt(date.getText().toString()),tmp);
                ssddb.insert(log);
            }
        });

        Button show = (Button) findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(ssddb.read());
            }
        });

    }
}

class SsdDB extends SQLiteOpenHelper{
    Calendar start = null;

    public SsdDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        start = Calendar.getInstance();
        start.set(2016, 12, 1, 0, 0, 0);
        long time = start.getTimeInMillis();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE LOG (LOG_USER_ID INTEGER,DEVICE_ID INTEGER,DATE INTEGER);");
        db.execSQL("CREATE TALBE USER(USER_USER_ID INTEGER,USER_USER_NAME TEXT);");
        db.execSQL("CREATE TABLE DEVICE(DEVICE_DEVICE_ID INTEGER,DEVICE_DEVICE_NAME TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(LogData log){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("INSERT INTO LOG VALUES(" + log.getId() + "," + log.getId() + "," + log.getDate() + ");");
        db.close();
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
        return result.toString();
    }
}