package com.example.koo.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;

import config.ConfigData;

public class MainActivity extends AppCompatActivity {
    ObjectOutputStream oout = null;
    ObjectInputStream oin = null;
    FileOutputStream fout = null;
    Intent intent = null;
    EditText input = null;
    EditText output = null;
    TextView result = null;
    ConfigDataManager dmgr = null;
    ConfigData data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(getApplicationContext(),Next.class);
        input = (EditText) findViewById(R.id.input);
        output = (EditText) findViewById(R.id.output);
        dmgr = ConfigDataManager.getInstance(this);
        this.data = dmgr.getData();
        result = (TextView) findViewById(R.id.result);
        Intent service = new Intent(getApplicationContext(),BackGround.class);
        startService(service);
    }

    public void save(View view){
        data.setAngle(data.getAngle() + 1);
        if(data.isAuto_update()){
            data.setAuto_update(false);
        }else{
            data.setAuto_update(true);
        }
        data.getUser().add(input.getText().toString());
        data.getDevice().add(output.getText().toString());
        dmgr.saveData(this.data);
    }

    public void load(View view){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("앵글 : "+this.data.getAngle());
        stringBuffer.append("\n자동 업데이트 : " + this.data.isAuto_update());
        stringBuffer.append("\n");
        for (String s :
                data.getUser()) {
            stringBuffer.append(s + " ");
        }
        stringBuffer.append("\n");
        for (String s :
                data.getDevice()) {
            stringBuffer.append(s + " ");
        }
        result.setText(stringBuffer);
    }

    public void log(View view){
        startActivity(intent);
    }
}
