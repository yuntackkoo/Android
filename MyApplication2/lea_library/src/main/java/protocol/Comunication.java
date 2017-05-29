package protocol;

/**
 * Created by 사용자 on 2017-05-29.
 */

public class Comunication extends Thread{
    private ComPacket comPacket = null;
    private String dn = null;
    private String port = null;
    private int seq_num = 0;
    private Packet send = null;
    private Packet recive = null;
    private boolean flag = true;

    public Comunication(String dn, String port) {
        this.dn = new String(dn);
        this.port = new String(port);
        flag = true;
    }

    @Override
    public void run() {
        if(flag) {
            comPacket = new TcpComPacket(dn, port);
        }
        send = new Packet();
        send.setCode(OperationCode.Join);
        send.setData();
        comPacket.setProcess(new PacketProcess() {
            @Override
            public void doProcess() {
                switch (recive.getCode()){
                    case OperationCode.Reponse:
                        break;
                }
            }
        });
    }
}
