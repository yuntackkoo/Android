package ssd.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

public class BTService {

    private static BluetoothAdapter mBTadapter;
    private static BluetoothDevice mRemoteDev; //연결할 장치
    private static BluetoothSocket mSocket=null;

    //블루투스 환경 체크
    private static void checkBluetooth(MainActivity ctx) {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 리턴
         * 이경우 Toast를 사용해 에러메시지 표시 */

        mBTadapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTadapter == null) {
            Toast.makeText(ctx, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
        } else if (!mBTadapter.isEnabled()) { // isEnable() : 블루투스 활성화 되었는지 확인
            mBTadapter.enable();
            //Intent intent_enablebt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(intent_enablebt, REQUEST_ENABLE_BT);
        } else {
            ctx.btTask.cancel(true);
            BTService.scanLeDevice(true);
        }
    }

    private static void scanLeDevice(final boolean enable) {

        }

    }


    /*
    //페어링 목록 받아오기
    static void searchBTlist(final MainActivity ctx) {
        // BluetoothDevice : 블루투스 장비에 대한 정보(이름, 주소 등) 제공하는 클래스
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 메소드
        final Set<BluetoothDevice> pairedDevices = mBTadapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            AlertDialog.Builder dialog_Paired = new AlertDialog.Builder(ctx);
            //dialog_Paired.setTitle("페어링 목록");

            List<String> pairedItem = new ArrayList<String>();
            for (BluetoothDevice device : pairedDevices) {
                pairedItem.add(device.getName());
                Log.d("Bluetooth", device.getName());
            }
            //pairedItem.add("취소");

            final CharSequence[] items = pairedItem.toArray(new CharSequence[pairedItem.size()]);
            // toArray : List 형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
            // toArray 함수를 이용해서 size 만큼 배열이 생성 되었다.
            pairedItem.toArray(new CharSequence[pairedItem.size()]);

            dialog_Paired.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    /*
                    if (item == pairedDevices.size()) { // 장치를 선택하지 않고 취소를 누른 경우
                        Toast.makeText(ctx, "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                    } else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.

                        connectToSelectedDev(ctx, items[item].toString()); // 페어링 장치와 연결
                    }
                });

            dialog_Paired.setNegativeButton("닫기",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(ctx, "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog_Paired.setCancelable(false); // 백스페이스 버튼 사용금지
            AlertDialog alert = dialog_Paired.create();
            alert.show();

        } else if (pairedDevices.size() == 0) {
            Toast.makeText(ctx, "페어링 된 장치가 없음", Toast.LENGTH_SHORT).show();
        }
    }

    //선택한 장치와 연결
    static void connectToSelectedDev (MainActivity ctx, String selectedDevName) {
        mRemoteDev = getDeviceFromBondedList(selectedDevName);
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
        UUID uuid = java.util.UUID.fromString("00001108-0000-1000-8000-00805F9B34FB");

        try {
            Toast.makeText(ctx, "연결", Toast.LENGTH_SHORT).show();
            mSocket = mRemoteDev.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
        } catch(Exception e) {
            Toast.makeText(ctx, "블루투스 연결 중 오류", Toast.LENGTH_SHORT).show();
            ctx.finish();

        }
    }

    //페어링 장치 얻기
    static BluetoothDevice getDeviceFromBondedList(String selectedDevName) {
        BluetoothDevice selectedDev = null;
        for(BluetoothDevice device : mBTadapter.getBondedDevices()) {
            if(selectedDevName.equals(device.getName())) {
                selectedDev = device;
                break;


            }
        }
        return selectedDev;
    }
    */
