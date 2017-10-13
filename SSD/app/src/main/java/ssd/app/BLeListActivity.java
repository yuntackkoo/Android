package ssd.app;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;

import ssd.app.bluetooth.BTCTemplateService;
import ssd.app.bluetooth.BleAdapter;
import ssd.app.bluetooth.BleManager;
import ssd.app.bluetooth.Constants;
import ssd.app.databinding.ActivityBlelistBinding;


public class BLeListActivity extends Activity {

    ActivityBlelistBinding binding_BLElist;

    static BTCTemplateService mService;
    private BluetoothAdapter mBtAdapter;
    private BleManager mBleManager;
    private ActivityHandler mActivityHandler;

    private static final long SCAN_PERIOD = 5 * 1000;

    private BleAdapter mLeDeviceListAdapter;
    private ArrayList<BluetoothDevice> arrDevices = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding_BLElist = DataBindingUtil.setContentView(this, R.layout.activity_blelist);
        BLeListActivity.this.setFinishOnTouchOutside(false);

        mActivityHandler = new ActivityHandler();

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mBleManager = BleManager.getInstance(getApplicationContext(), mActivityHandler);
        mBleManager.setScanCallback(mLeScanCallback);

        binding_BLElist.btnScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDiscovery();
                view.setVisibility(View.INVISIBLE);
            }
        });

        mLeDeviceListAdapter = new BleAdapter(BLeListActivity.this);
        binding_BLElist.BleScanlist.setAdapter(mLeDeviceListAdapter);
        binding_BLElist.BleScanlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBtAdapter.cancelDiscovery();
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;
//                final Intent intent = new Intent(BLeListActivity.this, BLeControlActivity.class);
//                intent.putExtra(BLeControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//                intent.putExtra(BLeControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//                startActivity(intent);
            }
        });

        doStartService();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doStopService();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
    }

    // ServiceConnection 인터페이스를 구현하는 객체를 생성
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((BTCTemplateService.ServiceBinder) service).getService();
            Log.d("BLeList", "service ready");
            initialize();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private void doStartService() {
        startService(new Intent(this, BTCTemplateService.class));
        bindService(new Intent(this, BTCTemplateService.class), mServiceConn, Context.BIND_AUTO_CREATE);
    } // 서비스 시작

    private void doStopService() {
        mService.finalizeService();
        stopService(new Intent(this, BTCTemplateService.class));
    } // 서비스 중단

    //초기화 메소드
    private void initialize() {
        Log.d("BLeList", "start initialize()");

        // BLE 지원 여부 체크
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        mService.setupService(mActivityHandler);

        // 블루투스가 켜져있지 않으면 실행
        if (!mService.isBluetoothEnabled()) {
            Intent intent_enablebt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent_enablebt, Constants.REQUEST_ENABLE_BT);
        }

        // 위치 권한 퍼미션 체크
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    // 디바이스 스캔 콜백
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("RSSI", Integer.toString(rssi));
                            mLeDeviceListAdapter.add(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                            arrDevices.add(device);
                        }
                    });
                }
            };

    private void doDiscovery() {
        Log.d("BLeListActivity", "doDiscovery()");
        arrDevices.clear();
        mLeDeviceListAdapter.clear();
        mLeDeviceListAdapter.notifyDataSetChanged();

        if (mBleManager.getState() == BleManager.STATE_SCANNING) {
            mBleManager.scanLeDevice(false);
        }

        mBleManager.scanLeDevice(true);

        mActivityHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding_BLElist.btnScanning.setVisibility(View.VISIBLE);
                mBleManager.scanLeDevice(false);
            }
        }, SCAN_PERIOD);
    }


    private void stopDiscovery() {
        //mBleManager.scanLeDevice(false);
    }

    public void onClick_btnscanning(View v) {
        // 안씀
    }

    public void onClick_btnblecancle(View v) {
        this.finish();
        mBleManager.scanLeDevice(false);
        if (mLeDeviceListAdapter != null) {
            mLeDeviceListAdapter.clear();
            mLeDeviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            }
            super.handleMessage(msg);
        }
    }
}