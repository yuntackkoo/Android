package ssd.app;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ssd.app.bluetooth.Constants;
import ssd.app.databinding.ActivityBlecontrolBinding;

import static ssd.app.BLeListActivity.mService;

public class BLeControlActivity extends AppCompatActivity {

    ActivityBlecontrolBinding binding_BLEControl;

    private long backKeyPressedTime = 0;
    private String sDevName, sDevAddr;

    private Intent intent_data;
    private String sMessage;
    private static TextView tv_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding_BLEControl = DataBindingUtil.setContentView(this, R.layout.activity_blecontrol);

        tv_chat = binding_BLEControl.tvChat;

        setConnection();

        // 전송 버튼
        binding_BLEControl.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sMessage = binding_BLEControl.etMessage.getText().toString();
                if (mService != null && sMessage != null && sMessage.length() > 0) {
                    mService.sendMessageToRemote(sMessage);
                    binding_BLEControl.etMessage.setText("");
                    binding_BLEControl.tvChat.append("Me : " + sMessage + "\n");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setConnection() {
        intent_data = getIntent();
        if (intent_data != null) {
            sDevName = intent_data.getExtras().getString("Device_Name");
            sDevAddr = intent_data.getExtras().getString("Device_Address");
            binding_BLEControl.tvDevname.setText("Device Name : " + sDevName);
            Log.d("setConnection() - ", sDevAddr);

            if (sDevAddr != null && mService != null) {
                mService.connectDevice(sDevAddr);

                if (sDevName.equals(mService.getDeviceName()) &&
                        sDevAddr.equals(mService.getDeviceAddr())) {
                    // 인텐트로 받은 장치 이름, 주소를 커넥트시 저장된것과 일치하면
                    binding_BLEControl.tvDevname.append(" (연결됨)");

                }
            }
        }
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(BLeControlActivity.this,
                    "연결을 종료하시려면 한번 더 누르세요!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            finish();
            //TODO 종료했을때 장치와 연결 해제
        }
    } // 백키 2번 종료

    private static void showMessage(String message) {
        Log.d("READTEST", "4");
        final int NEW_LINE_INTERVAL = 1000;
        long mLastReceivedTime = 0L;
        if (message != null && message.length() > 0) {
            long current = System.currentTimeMillis();
            Log.d("READTEST", String.valueOf(current));
            if (current - mLastReceivedTime > NEW_LINE_INTERVAL) {
                tv_chat.append("Device : ");
                Log.d("READTEST", "5");
            }
            tv_chat.append(message + "\n");
            Log.d("READTEST", "6");
            mLastReceivedTime = current;
        }
    }

    public static class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ_CHAT_DATA:
                    Log.d("READTEST", "2");
                    if (msg.obj != null) {
                        Log.d("READTEST", "3");
                        BLeControlActivity.showMessage((String) msg.obj);
                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

}
