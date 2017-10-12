package ssd.app.bluetooth;

import android.os.Handler;
import android.util.Log;

/**
 * If you want to send something to remote
 * add methods here.
 * <p>
 * begin() : Initialize parameters
 * setXxxx() : Add methods as you wish
 * settingFinished() : Every data is ready.
 * sendTransaction() : Send to remote
 */
public class TransactionBuilder {

    private static final String TAG = "TransactionBuilder";

    private BleManager mBleManager = null;
    private Handler mHandler = null;

    public TransactionBuilder(BleManager bm, Handler errorHandler) {
        mBleManager = bm;
        mHandler = errorHandler;
    }

    public Transaction makeTransaction() {
        return new Transaction();
    }

    public class Transaction {

        public static final int MAX_MESSAGE_LENGTH = 16;

        // Transaction instance status
        private static final int STATE_NONE = 0;        // Instance created
        private static final int STATE_BEGIN = 1;        // Initialize transaction
        private static final int STATE_SETTING_FINISHED = 2;    // End of setting parameters
        private static final int STATE_TRANSFERED = 3;    // End of sending transaction data
        private static final int STATE_ERROR = -1;        // Error occurred

        // Transaction parameters
        private int mState = STATE_NONE;
        private byte[] mBuffer = null;
        private String mMsg = null;


        /**
         * Make new transaction instance
         */
        public void begin() {
            mState = STATE_BEGIN;
            mMsg = null;
            mBuffer = null;
        }

        /**
         * Set string to send
         *
         * @param msg String to send
         */
        public void setMessage(String msg) {
            // TODO: do what you want
            mMsg = msg;
        }

        /**
         * Ready to send data to remote
         */
        public void settingFinished() {
            mState = STATE_SETTING_FINISHED;
            mBuffer = mMsg.getBytes();
        }

        /**
         * Send packet to remote
         *
         * @return boolean        is succeeded
         */
        public boolean sendTransaction() {
            if (mBuffer == null || mBuffer.length < 1) {
                Log.e(TAG, "##### Ooooooops!! No sending buffer!! Check command!!");
                return false;
            }

            // TODO: For debug. Comment out below lines if you want to see the packets

            if (mBuffer.length > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("Message : ");

                for (int i = 0; i < mBuffer.length; i++) {
                    sb.append(String.format("%02X, ", mBuffer[i]));
                }

                Log.d(TAG, " ");
                Log.d(TAG, sb.toString());
            }


            if (mState == STATE_SETTING_FINISHED) {
                if (mBleManager != null) {
                    // Check that we're actually connected before trying anything
                    if (mBleManager.getState() == BleManager.STATE_CONNECTED) {
                        // Check that there's actually something to send
                        if (mBuffer.length > 0) {
                            // Get the message bytes and tell the BleManager to write
                            mBleManager.write(null, mBuffer);

                            mState = STATE_TRANSFERED;
                            return true;
                        }
                        mState = STATE_ERROR;
                    }
                    // Report result
                    mHandler.obtainMessage(Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED).sendToTarget();
                }
            }
            return false;
        }

        /**
         * Get buffers to send to remote
         */
        public byte[] getPacket() {
            if (mState == STATE_SETTING_FINISHED) {
                return mBuffer;
            }
            return null;
        }

    }    // End of class Transaction

}
