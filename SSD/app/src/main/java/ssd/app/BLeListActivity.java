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
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;

import ssd.app.databinding.ActivityBlelistBinding;

public class BLeListActivity extends Activity {

    ActivityBlelistBinding binding_BLElist;
    private BLeAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBTAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

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

        // 블루투스 어댑터를 초기화한다
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBTAdapter = bluetoothManager.getAdapter();

        if (mBTAdapter == null) {
            Toast.makeText(this, "블루투스가 지원되지 않는 기기입니다..", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, permissionCheck);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionCheck2);

        binding_BLElist.gattServicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;
                final Intent intent_Itemclicked =
                        new Intent(BLeListActivity.this, BLeControlActivity.class);
                intent_Itemclicked.putExtra("DEVICE_NAME", device.getName());
                intent_Itemclicked.putExtra("DEVICE_ADDRESS", device.getAddress());
                if (mScanning) {
                    mBTAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                startActivity(intent_Itemclicked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 블루투스 어댑터 사용중이 아니면 실행
        if (!mBTAdapter.isEnabled()) {
            if (!mBTAdapter.isEnabled()) {
                Intent intent_enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent_enableBT, REQUEST_ENABLE_BT);
            }
        }

        // 뷰 어댑터 초기화
        mLeDeviceListAdapter = new BLeAdapter(BLeListActivity.this);
        binding_BLElist.gattServicesList.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
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

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Toast.makeText(BLeListActivity.this, "스캔 완료", Toast.LENGTH_SHORT).show();
                    mBTAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBTAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBTAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
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
        mLeDeviceListAdapter.clear();
        scanLeDevice(false);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    } // 백키 비활성화

}