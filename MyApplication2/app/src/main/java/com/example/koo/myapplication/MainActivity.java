package com.example.koo.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import config.ConfigData;
import protocol.Comunication;

public class MainActivity extends AppCompatActivity {
    Intent next = null;
    EditText input = null;
    EditText output = null;
    TextView result = null;
    ConfigDataManager dmgr = null;
    ConfigData data = null;
    Comunication com = null;
    EditText sendbyte = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        next = new Intent(getApplicationContext(),Next.class);
        input = (EditText) findViewById(R.id.input);
        output = (EditText) findViewById(R.id.output);
        dmgr = ConfigDataManager.getInstance(this);
        this.data = dmgr.getData();
        result = (TextView) findViewById(R.id.result);
        sendbyte = (EditText) findViewById(R.id.sendText);
        Intent service = new Intent(getApplicationContext(),BackGround.class);
        //startService(service);
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

    public void turnOn(View view){
        startActivity(next);
    }

    public void testOff(View view){
        com = new Comunication("192.168.43.174","255",(byte)0);
        com.start();
    }

    public void send(View view){
        com.send(sendbyte.getText().toString().getBytes());
    }

    public void log(View view){
        startActivity(next);
    }

}