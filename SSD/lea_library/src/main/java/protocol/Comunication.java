package protocol;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by 사용자 on 2017-05-29.
 */

public class Comunication extends Thread {
    private ComPacket comPacket = null;
    private String dn = null;
    private String port = null;
    private int seq_num = 0;
    private int id = 0;
    private Packet send = null;
    private Packet recive = null;
    private boolean flag = true;
    private Loging loging = null;
    private boolean connected = false;

    public Comunication(String dn, String port,byte id) {
        this.dn = new String(dn);
        this.port = new String(port);
        this.id = id;
        flag = true;
    }

    public byte getThisId() {
        return (byte)id;
    }

    @Override
    public void run() {
        if(flag) {
            comPacket = new TcpComPacket(dn, port);
            connected = true;
        }
        comPacket.setProcess
                (new PacketProcess() {
                    @Override
                    public void doProcess() {
                        //boolean invail = false;
                        boolean invail = true;
                        recive = comPacket.getCurrent();
                        if (seq_num != 0) {
                            //invail = recive.comp(seq_num);
                        }
                        if (invail && recive != null) {
                            switch (recive.getCode()) {
                                //응답
                                case OperationCode.Reponse:
                                    seq_num = recive.getNonce();
                                    System.out.println(seq_num + " 시퀸스 넘버");
                                    Packet p = new Packet();
                                    p.setCode(OperationCode.UnLock);
                                    comPacket.send(p);
                                    break;
                                //초기 등록 또는 추가 등록시 키교환 확인
                                case OperationCode.Confirm_KeyEx:
                                    break;
                                //키 교환 요구 받을시 키 제공
                                case OperationCode.KeyOffer:
                                    comPacket.getCryptoModule().setKey(recive.getData());
                                    break;
                                //로그 요청 받을시 로그 응답
                                case OperationCode.Offer_Data:
                                    loging.loging();
                                    break;
                                //다른 기기가 문을 열때
                                case OperationCode.Unlock_Other:
                                    loging.loging();
                                    break;
                            }
                        }
                        seq_num++;
                        recive = null;
                    }

                }
        );
        send = new Packet();
        send.setCode(OperationCode.Join);
        send.setId(id);
        comPacket.send(send);
        for(;comPacket.isConnect();) {
            comPacket.receive();
        }
    }

    public void send(Packet send) {
        if(comPacket != null){
            this.seq_num++;
            send.setNonce(this.seq_num);
            comPacket.send(send);
        }
    }

    public void setLoging(Loging log){
        this.loging = log;
    }

    public ComPacket getComPacket() {
        return comPacket;
    }

    public String getDn() {
        return dn;
    }

    public String getPort() {
        return port;
    }

    public int getSeq_num() {
        return seq_num;
    }

    public Packet getSend() {
        return send;
    }

    public Packet getRecive() {
        return recive;
    }

    public boolean isFlag() {
        return flag;
    }

    public boolean isConnected() {
        return connected;
    }

}