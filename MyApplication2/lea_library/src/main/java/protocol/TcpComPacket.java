package protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class TcpComPacket extends ComPacket{
	private SocketAddress socket = null;
	private java.net.Socket tcp_send = null;
	private ServerSocket soc = null;
	private BufferedOutputStream out = null;
	private BufferedInputStream in = null;
	private byte[] buffer = new byte[32];
	private PacketProcess process = null;
	private int port = 0;
	
	public TcpComPacket(String dn, String port) {
		try{
			this.port = Integer.parseInt(port);
			tcp_send = new Socket();
			socket = new InetSocketAddress(dn,this.port);
			tcp_send.connect(socket,20000);
			in = new BufferedInputStream(tcp_send.getInputStream());
			out = new BufferedOutputStream(tcp_send.getOutputStream());
			tcp_send.setSoTimeout(1000);
		}
		catch (ConnectException e){

		}
		catch(IOException e){
		}

	}

	@Override
	public void send(byte[] send) {
		try {
			out.write(send);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Packet receive() {
		int check = 0;
		boolean flag = true;
		for(;flag;){
			try {
				in.read(buffer);
				flag = false;
			} catch (SocketTimeoutException e){
				if(check < 15){
					check++;
					System.out.println("시간 초과?");
				}
				else{
					System.out.println("시간 초과");
					flag = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.process.doProcess();
		return null;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	@Override
	public void setProcess(PacketProcess process) {
		this.process = process;
	}
}
