package ssd.app.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import protocol.ComPacket;
import protocol.Packet;
import protocol.PacketProcess;

/**
 * Created by admin on 2017-10-16.
 */

public class BleComPacket extends ComPacket {

    private static final String TAG = "BleComPacket";

    public static final int STATE_ERROR = -1;
    public static final int STATE_NONE = 0;        // Initialized
    public static final int STATE_IDLE = 1;        // Not connected
    public static final int STATE_SCANNING = 2;    // Scanning
    public static final int STATE_CONNECTING = 13;    // Connecting
    public static final int STATE_CONNECTED = 16;    // Connected

    private byte[] buffer = new byte[33];

    private static Context mContext = null;
    private static BleManager mBleManager = null;        // Singleton pattern

    private final BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = null;
    private BluetoothGatt mBluetoothGatt = null;

    private ArrayList<BluetoothGattService> mGattServices
            = new ArrayList<BluetoothGattService>();
    private BluetoothGattService mDefaultService = null;
    private ArrayList<BluetoothGattCharacteristic> mGattCharacteristics
            = new ArrayList<BluetoothGattCharacteristic>();
    private ArrayList<BluetoothGattCharacteristic> mWritableCharacteristics
            = new ArrayList<BluetoothGattCharacteristic>();
    private BluetoothGattCharacteristic mDefaultChar = null;

    private BluetoothDevice mDefaultDevice = null;

    private PacketProcess process = null;

    private int mState = -1;

    public BleComPacket(Context ctx, boolean bAutoReconnect, String address) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mContext = ctx;

        if (mContext == null)
            return;
        connectGatt(ctx, bAutoReconnect, address);

    }

    public boolean connectGatt(Context c, boolean bAutoReconnect, String address) {
        if (c == null || address == null)
            return false;

        if (mBluetoothGatt != null && mDefaultDevice != null
                && address.equals(mDefaultDevice.getAddress())) {
            if (mBluetoothGatt.connect()) {
                mState = STATE_CONNECTING;
                return true;
            }
        }
        Log.d("Gatt Connect", "3");
        BluetoothDevice device =
                BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        if (device == null) {
            Log.d("checkGattServices", "# Device not found.  Unable to connect.");
            return false;
        }

        mGattServices.clear();
        mGattCharacteristics.clear();
        mWritableCharacteristics.clear();
        Log.d("Gatt Connect", "4");
        mBluetoothGatt = device.connectGatt(c, bAutoReconnect, mGattCallback);
        mDefaultDevice = device;

        mState = STATE_CONNECTING;
        Log.d("Gatt Connect", "5");
        return true;
    }


    @Override
    public boolean send(Packet send) {
        send.fillPadding();

        send.getPacket();

        return false;
    }

    @Override
    public void receive() {

        if (isConnect()) {
            this.process.doProcess(super.getCom());
        }
    }

    @Override
    public boolean isConnect() {
        return false;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mState = STATE_CONNECTED;
                Log.d(TAG, "# Connected to GATT server.");
//                mHandler.obtainMessage(MESSAGE_STATE_CHANGE, STATE_CONNECTED, 0).sendToTarget();

                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mState = STATE_IDLE;
                Log.d(TAG, "# Disconnected from GATT server.");
//                mHandler.obtainMessage(MESSAGE_STATE_CHANGE, STATE_IDLE, 0).sendToTarget();
                mBluetoothGatt = null;
                mGattServices.clear();
                mDefaultService = null;
                mGattCharacteristics.clear();
                mWritableCharacteristics.clear();
                mDefaultChar = null;
                mDefaultDevice = null;
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "# New GATT service discovered.");
                checkGattServices(gatt.getServices());
            } else {
                Log.d(TAG, "# onServicesDiscovered received: " + status);
            }
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // We've received data from remote
                Log.d(TAG, "# Read characteristic: " + characteristic.toString());

            	/*
                 * onCharacteristicChanged callback receives same message
            	 *
            	final byte[] data = characteristic.getValue();
            	if (data != null && data.length > 0) {
            		final StringBuilder stringBuilder = new StringBuilder(data.length);
            		//for(byte byteChar : data)
            		//	stringBuilder.append(String.format("%02X ", byteChar));
            		stringBuilder.append(data);
            		Log.d("checkGattServices",TAG, stringBuilder.toString());

            		mHandler.obtainMessage(MESSAGE_READ, new String(data)).sendToTarget();
            	}

            	if(mDefaultChar == null && isWritableCharacteristic(characteristic)) {
            		mDefaultChar = characteristic;
            	}
            	*/
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // We've received data from remote
            Log.d(TAG, "# onCharacteristicChanged: " + characteristic.toString());

            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                //for(byte byteChar : data)
                //	stringBuilder.append(String.format("%02X ", byteChar));
                stringBuilder.append(data);
                Log.d(TAG, stringBuilder.toString());
                Log.d("READTEST", "0");
//                mHandler.obtainMessage(MESSAGE_READ, new String(data)).sendToTarget();
            }

//            if (mDefaultChar == null && isWritableCharacteristic(characteristic)) {
//                mDefaultChar = characteristic;
//            }
        }

        ;
    };

    private int checkGattServices(List<BluetoothGattService> gattServices) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d("checkGattServices", "# BluetoothAdapter not initialized");
            return -1;
        }

        for (BluetoothGattService gattService : gattServices) {
            // Default service info
            Log.d("checkGattServices", "# GATT Service: " + gattService.toString());

            // Remember service
            mGattServices.add(gattService);

            // Extract characteristics
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                // Remember characteristic
                mGattCharacteristics.add(gattCharacteristic);
                Log.d("checkGattServices", "# GATT Char: " + gattCharacteristic.toString());

                boolean isWritable = isWritableCharacteristic(gattCharacteristic);
                if (isWritable) {
                    mWritableCharacteristics.add(gattCharacteristic);
                }

                boolean isReadable = isReadableCharacteristic(gattCharacteristic);
                if (isReadable) {
                    readCharacteristic(gattCharacteristic);
                }

                if (isNotificationCharacteristic(gattCharacteristic)) {
                    setCharacteristicNotification(gattCharacteristic, true);
                    if (isWritable && isReadable) {
                        mDefaultChar = gattCharacteristic;
                    }
                }
            }
        }

        return mWritableCharacteristics.size();
    }

    private boolean isWritableCharacteristic(BluetoothGattCharacteristic chr) {
        if (chr == null) return false;

        final int charaProp = chr.getProperties();
        if (((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0) {
            Log.d("checkGattServices", "# Found writable characteristic");
            return true;
        } else {
            Log.d("checkGattServices", "# Not writable characteristic");
            return false;
        }
    }

    private boolean isReadableCharacteristic(BluetoothGattCharacteristic chr) {
        if (chr == null) return false;

        final int charaProp = chr.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            Log.d("checkGattServices", "# Found readable characteristic");
            return true;
        } else {
            Log.d("checkGattServices", "# Not readable characteristic");
            return false;
        }
    }

    private boolean isNotificationCharacteristic(BluetoothGattCharacteristic chr) {
        if (chr == null) return false;

        final int charaProp = chr.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            Log.d("checkGattServices", "# Found notification characteristic");
            return true;
        } else {
            Log.d("checkGattServices", "# Not notification characteristic");
            return false;
        }
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d("checkGattServices", "# BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d("checkGattServices", "# BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

}
