package ssd.app;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;

import ssd.app.bluetooth.BleAdapter;
import ssd.app.databinding.ActivityBlelistBinding;


public class BLeListActivity extends Activity {

    ActivityBlelistBinding binding_BLElist;
    private BleAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBtAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 8 * 1000;

    private ArrayList<BluetoothDevice> arrDevices = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding_BLElist = DataBindingUtil.setContentView(this, R.layout.activity_blelist);
        BLeListActivity.this.setFinishOnTouchOutside(false);

        mHandler = new Handler();

        // 장치가 BLE 지원하는지 안하는지 확인한다
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE가 지원되지 않는 기기입니다..", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 위치 권한
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

        // 블루투스 어댑터를 초기화한다
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();

        // 블루투스 어댑터 사용중이 아니면 실행
        if (!mBtAdapter.isEnabled()) {
            Intent intent_enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent_enableBT, REQUEST_ENABLE_BT);
        }


        binding_BLElist.BleScanlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mScanning) {
                    mBtAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;
                final Intent intent = new Intent(BLeListActivity.this, BLeControlActivity.class);
                intent.putExtra(BLeControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(BLeControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                startActivity(intent);
            }
        });
                /*
                final Intent intent_Itemclicked =
                        new Intent(BLeListActivity.this, BLeControlActivity.class);
                intent_Itemclicked.putExtra(BLeControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent_Itemclicked.putExtra(BLeControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                startActivity(intent_Itemclicked);
                */
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 블루투스 초기화 확인
        if (mBtAdapter == null) {
            Toast.makeText(this, "블루투스 어댑터를 찾을 수 없음", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            // 뷰 어댑터 초기화
            mLeDeviceListAdapter = new BleAdapter(BLeListActivity.this);
            binding_BLElist.BleScanlist.setAdapter(mLeDeviceListAdapter);
            scanLeDevice(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLeDeviceListAdapter.clear();
        scanLeDevice(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLeDeviceListAdapter.clear();
        scanLeDevice(false);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mLeDeviceListAdapter.clear();
        scanLeDevice(false);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Toast.makeText(BLeListActivity.this, "스캔 완료", Toast.LENGTH_SHORT).show();
                    mBtAdapter.stopLeScan(mLeScanCallback);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBtAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBtAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private boolean checkDuplicated(BluetoothDevice device) {
        for (BluetoothDevice dvc : arrDevices) {
            if (device.getAddress().equalsIgnoreCase(dvc.getAddress())) {
                return true;
            }
        }
        return false;
    }

    // 디바이스 스캔 콜백
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    Log.d("RSSI", Integer.toString(rssi));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!checkDuplicated(device)) {
                                mLeDeviceListAdapter.add(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                                arrDevices.add(device);
                            }
                        }
                    });
                }
            };

    public void onClick_btnscanning(View v) {
        if (!mScanning) {
            mLeDeviceListAdapter.clear();
            scanLeDevice(true);
        } else if (mScanning) {
//            mLeDeviceListAdapter.clear();
//            scanLeDevice(false);
            Toast.makeText(this, "스캔 중이니까 기다리세욧!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick_btnblecancle(View v) {
        this.finish();
        if (mLeDeviceListAdapter != null)
            mLeDeviceListAdapter.clear();
        scanLeDevice(false);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    } // 백키 비활성화

}