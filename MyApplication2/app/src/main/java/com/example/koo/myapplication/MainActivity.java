package com.example.koo.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;

import kr.re.nsr.crypto.BlockCipherMode;
import kr.re.nsr.crypto.mode.ECBMode;
import kr.re.nsr.crypto.symm.LEA;

public class MainActivity extends AppCompatActivity {

    EditText msg = null;
    send t = null;
    EditText ip = null;
    EditText port = null;
    MessageDigest sh = null;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = new send();
        msg = (EditText) findViewById(R.id.msg);
        ip = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        intent = new Intent(getApplicationContext(),Next.class);

        ActionBar action = this.getSupportActionBar();

        action.setDisplayShowCustomEnabled(true);
        action.setDisplayHomeAsUpEnabled(false);
        action.setDisplayShowHomeEnabled(false);
        action.setDisplayShowTitleEnabled(false);
        action.setDisplayUseLogoEnabled(false);

        View actionlay = LayoutInflater.from(this).inflate(R.layout.layout,null);
        action.setCustomView(actionlay);

    }

    public void test(View view){
        Toast.makeText(getApplicationContext() ,"메세지 전송",Toast.LENGTH_LONG).show();
        t.start();
    }

    public void log(View view){
        startActivity(intent);
    }



    class send extends Thread
    {
        DatagramSocket soc = null;
        DatagramPacket packet = null;
        byte[] byt = new byte[2000];
        InetAddress add = null;

        public send()
        {
            byt[0] = 0;
            try{
                soc = new DatagramSocket();
                sh = MessageDigest.getInstance("SHA-1");
            }
            catch (Exception e)
            {}
        }

        @Override
        public void run() {

            for(;;) {
                try {
                    add = InetAddress.getByName(ip.getText().toString());
                    packet = new DatagramPacket(msg.getText().toString().getBytes(), msg.getText().toString().length(), add, Integer.parseInt(port.getText().toString()));
                    soc.send(packet);
                    sleep(1000);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "에러발생", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
