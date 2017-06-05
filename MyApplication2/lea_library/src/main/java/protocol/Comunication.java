package protocol;

/**
 * Created by 사용자 on 2017-05-29.
 */

public class Comunication extends Thread{
    private ComPacket comPacket = null;
    private String dn = null;
    private String port = null;
    private int seq_num = 0;
    private int id = 0;
    private Packet send = null;
    private Packet recive = null;
    private boolean flag = true;


    public Comunication(String dn, String port,byte id) {
        this.dn = new String(dn);
        this.port = new String(port);
        this.id = id;
        flag = true;
    }

    @Override
    public void run() {
        if(flag) {
            comPacket = new TcpComPacket(dn, port);
        }

        send = new Packet();
        send.setCode(OperationCode.Join);
        send.setId(id);
        send.fillPadding();
        comPacket.send(send);
        comPacket.receive();
        comPacket.setProcess(new PacketProcess() {
            @Override
            public void doProcess() {
                //boolean invail = false;
                boolean invail = true;
                recive = comPacket.getCurrent();
                if(seq_num != 0){
                    //invail = recive.comp(seq_num);
                }
                if(invail) {
                    switch (recive.getCode()) {
                        //응답
                        case OperationCode.Reponse:
                            seq_num = recive.getNonce();
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
                            break;
                    }
                }
                seq_num++;
            }

        }
        );
    }

    public void send(Packet send) {
        if(comPacket != null){
            this.seq_num++;
            send.setNonce(this.seq_num);
            comPacket.send(send);
        }
    }

}