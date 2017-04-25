package protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

import javax.naming.TimeLimitExceededException;

public class TcpComPacket extends ComPacket{
	private SocketAddress socket = null;
	private java.net.Socket tcp_send = null;
	private java.net.Socket tcp_receive = null;
	private ServerSocket soc = null;
	private BufferedOutputStream out = null;
	private BufferedInputStream in = null;
	private byte[] buffer = new byte[32];
	private PacketProcess process = null;
	private int port = 0;
	
	public TcpComPacket(String dn, String port) {
		try{
			byte[] add = {(byte)192,(byte)168,43,(byte)159};
			this.port = Integer.parseInt(port);
			socket = new InetSocketAddress(InetAddress.getByAddress(add),45621);
			tcp_send.connect(socket,20000);
			in = new BufferedInputStream(tcp_send.getInputStream());
			out = new BufferedOutputStream(tcp_send.getOutputStream());
			tcp_send.setSoTimeout(1000);
		}
		catch(IOException e){

		}
		catch (SecurityException e){
		}
	}

	public TcpComPacket(String port){
		try {
			this.port = Integer.parseInt(port);
			soc = new ServerSocket(this.port);
			tcp_receive = soc.accept();
		} catch (IOException e) {
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
				if(check < 3){
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

	@Override
	protected void finalize() throws Throwable {
		if(soc != null){
			soc.close();
		}
		if(tcp_receive != null){
			tcp_receive.close();
		}
		if(tcp_send != null){
			tcp_send.close();
		}
		if(in != null){
			in.close();
		}
		if(out != null){
			out.close();
		}
		super.finalize();
	}
}
