package protocol;

public class Socket extends Thread {
	TcpComPacket udp = null;
	SsdCryptoModule passwd = null;
	SsdCryptoModule key = null;
	String dest = null;
	String port = null;
	int i = 3;
	
	public Socket(String dn, int port){
		udp = new TcpComPacket(dest, this.port);
	}
	
	public void handShake(){
	}
}