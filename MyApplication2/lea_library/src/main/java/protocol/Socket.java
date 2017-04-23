package protocol;

public class Socket extends Thread {
	UdpComPacket udp = null;
	SsdCryptoModule passwd = null;
	SsdCryptoModule key = null;
	String dest = null;
	String port = null;
	int i = 3;
	
	public Socket(){
		udp = new UdpComPacket(dest, port);
	}
	
	public void handShake(){
	}
}
