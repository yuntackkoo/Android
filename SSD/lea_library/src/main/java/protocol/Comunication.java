package protocol;

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
    private Comunication me = null;
    private PacketProcess p = null;

    public Comunication(String dn, String port, byte id) {
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
        comPacket.setCom(this);
        comPacket.setProcess(this.p);
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
            System.out.println(send.getCode());
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

    public void setComPacket(ComPacket comPacket) {
        this.comPacket = comPacket;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setSeq_num(int seq_num) {
        this.seq_num = seq_num;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSend(Packet send) {
        this.send = send;
    }

    public void setRecive(Packet recive) {
        this.recive = recive;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Loging getLoging() {
        return loging;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Comunication getMe() {
        return me;
    }

    public void setMe(Comunication me) {
        this.me = me;
    }

    public void setP(PacketProcess p) {
        this.p = p;
    }
}